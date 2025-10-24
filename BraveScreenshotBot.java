import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.nio.file.Paths;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class BraveScreenshotBot {

    private static final String BOT_TOKEN = "7802518432:AAEhRv8IUvYDNAlmgVj6BcWppq-kNAiDwJQ";
    private static final String CHAT_ID   = "-1003181231519";
    private static final String USERNAME  = "VikasSingh96";
    private static final String PASSWORD  = "Abcd@1234";

    private static final String CHROME_BINARY = System.getenv().getOrDefault("CHROME_BIN", "/usr/bin/google-chrome");
    private static final String PROFILE_DIR   = "C:\\ChromeBotProfile";
    private static final String PB77_CASINO   = "https://www.pb77.co/casino";
    private static final String EVO_ALL_GAMES = "https://babylonorbit2.evo-games.com/frontend/evo/r2/#category=all_games&my_list=true";

    private static final int  SLOW_WAIT_SEC = 40;
    private static final long POLL_MS       = 500;
    private static final int  SCREENSHOT_EVERY_SEC = 30;

    private static WebDriver driver;
    private static long startMs;

    public static void main(String[] args) {
        sendTG("🚀 Starting Chrome Screenshot Bot...");
        startMs = System.currentTimeMillis();

        while (true) {
            try {
                setupDriver();
                loginAndOpenAllGames();

                while (true) {
                    // 🔍 check session expired popup
                    if (isSessionExpired()) break;

                    forceOnlyAllGamesTab();

                    if (!currentUrlContains(EVO_ALL_GAMES)) {
                        driver.navigate().to(EVO_ALL_GAMES);
                        sleep(2);
                        if (!currentUrlContains(EVO_ALL_GAMES)) {
                            sendTG("⏸ Not on target page, skipping screenshot… Restarting login.");
                            break;
                        }
                    }

                    String uptime = uptimeString();
                    takeScreenshotAndSend(uptime);
                    driver.navigate().refresh();
                    sleep(2);
                    sleep(SCREENSHOT_EVERY_SEC);
                }
            } catch (Exception e) {
                sendTG("💥 Fatal error: " + safe(e.getMessage()) + " — restarting…");
            } finally {
                try { if (driver != null) driver.quit(); } catch (Exception ignore) {}
                sendTG("🔁 Restarting browser and re-login in 3s…");
                sleep(3);
            }
        }
    }

    private static void setupDriver() {
        ChromeOptions opt = new ChromeOptions();
        opt.setBinary(CHROME_BINARY);
        opt.addArguments("--user-data-dir=" + PROFILE_DIR);
        opt.addArguments("--profile-directory=Bot");
        opt.addArguments("--start-maximized");
        opt.addArguments("--disable-notifications");
        opt.addArguments("--disable-popup-blocking");
        opt.addArguments("--no-sandbox");
        opt.addArguments("--disable-dev-shm-usage");
        opt.addArguments("--log-level=3");

        driver = new ChromeDriver(opt);
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(60));
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(0));
    }

    private static void loginAndOpenAllGames() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(SLOW_WAIT_SEC), Duration.ofMillis(POLL_MS));
        driver.get(PB77_CASINO);
        sleep(2);

        if (!isLoggedIn()) {
            sendTG("🔐 Logging in…");
            clickWithWait(wait, By.xpath("//*[@id=\"root\"]/div[2]/header/div[2]/button[1]"));
            sleep(1);
            clickWithWait(wait, By.xpath("/html/body/div[5]/div/div[2]/div/div[1]/div/div[2]/div[1]/div/div/div[4]/div"));
            sleep(1);

            WebElement user = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("/html/body/div[5]/div/div[2]/div/div[1]/div/div[2]/div[2]/form/div[1]/div/div[2]/div/div/div/span/input")));
            user.clear(); user.sendKeys(USERNAME);

            WebElement pass = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("/html/body/div[5]/div/div[2]/div/div[1]/div/div[2]/div[2]/form/div[2]/div/div[2]/div/div/span/input")));
            pass.clear(); pass.sendKeys(PASSWORD);
            sleep(1);

            clickWithWait(wait, By.xpath("/html/body/div[5]/div/div[2]/div/div[1]/div/div[2]/div[2]/form/div[3]/div/div/div/div/button"));
            sleep(2);
        } else {
            sendTG("✅ Already logged-in.");
        }

        // remove modals
        try {
            ((JavascriptExecutor) driver).executeScript("document.querySelectorAll('.ant-modal-wrap').forEach(e => e.remove());");
            sendTG("🧹 Removed any blocking modal popups.");
        } catch (Exception ignore) {}

        // open Live Casino
        sendTG("🎰 Opening Live Casino…");
        try {
            WebElement casinoMenu = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//li[.//span[contains(.,'Casino')]]//i")));
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'});", casinoMenu);
            casinoMenu.click();
            sleep(2);

            WebElement liveCasino = wait.until(ExpectedConditions.presenceOfElementLocated(
                    By.xpath("//a[normalize-space()='Live Casino']")));
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'});", liveCasino);
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", liveCasino);
            sleep(3);
        } catch (Exception e) {
            sendTG("⚠️ Live Casino click failed: " + safe(e.getMessage()));
            throw e;
        }

        // open Baccarat
        sendTG("🕹️ Searching Golden Wealth Baccarat…");
        boolean clicked = false;
        try {
            for (int i = 0; i < 15 && !clicked; i++) {
                ((JavascriptExecutor) driver).executeScript("window.scrollBy(0, 500);");
                sleep(1);

                List<WebElement> buttons = driver.findElements(By.xpath(
                        "//*[contains(translate(.,'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'golden wealth baccarat')]" +
                                "/ancestor::div[contains(@class,'game_card') or contains(@class,'ant-card') or contains(@class,'game_item')][1]" +
                                "//button[contains(@class,'play_btn')]"
                ));

                if (!buttons.isEmpty()) {
                    WebElement play = buttons.get(0);
                    ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'});", play);
                    sleep(1);
                    ((JavascriptExecutor) driver).executeScript("arguments[0].click();", play);
                    sendTG("✅ Clicked Golden Wealth Baccarat Play button.");
                    clicked = true;
                    break;
                }
            }
        } catch (Exception e) {
            sendTG("⚠️ Baccarat click failed: " + safe(e.getMessage()));
        }

        if (!clicked) {
            try {
                WebElement play = driver.findElement(By.xpath("//button[contains(@class,'play_btn')]/span[text()='Play']"));
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", play);
                sendTG("✅ Clicked fallback Play button.");
            } catch (Exception ex) {
                sendTG("❌ Golden Wealth Baccarat not found after scrolling.");
            }
        }

        sleep(7);
        closeNonAllGamesAndGoAllGames();

        if (!currentUrlContains(EVO_ALL_GAMES)) {
            driver.navigate().to(EVO_ALL_GAMES);
            sleep(2);
        }
        sendTG("🎯 Ready on ALL_GAMES page.");
    }

    private static boolean isLoggedIn() {
        try { driver.findElement(By.xpath("//button[normalize-space()='Logout']")); return true; }
        catch (Exception ignore) { return false; }
    }

    // ✅ SESSION EXPIRED FIX – closes browser instantly and restarts login
    private static boolean isSessionExpired() {
        try {
            if (detectSessionExpired(driver)) return true;
            List<WebElement> iframes = driver.findElements(By.tagName("iframe"));
            for (WebElement frame : iframes) {
                try {
                    driver.switchTo().frame(frame);
                    if (detectSessionExpired(driver)) {
                        driver.switchTo().defaultContent();
                        return true;
                    }
                    driver.switchTo().defaultContent();
                } catch (Exception ignore) {
                    driver.switchTo().defaultContent();
                }
            }
        } catch (Exception ignore) {}
        driver.switchTo().defaultContent();
        return false;
    }

    private static boolean detectSessionExpired(WebDriver ctx) {
        try {
            WebElement expired = ctx.findElement(
                    By.xpath("//*[contains(translate(.,'abcdefghijklmnopqrstuvwxyz','ABCDEFGHIJKLMNOPQRSTUVWXYZ'),'SESSION EXPIRED')]"));
            if (expired.isDisplayed()) {
                sendTG("⚠️ SESSION EXPIRED detected — closing browser and re-login will start.");
                try { driver.quit(); } catch (Exception ignore) {}
                return true;
            }
        } catch (Exception ignore) {}
        return false;
    }

    private static void forceOnlyAllGamesTab() {
        closeNonAllGamesAndGoAllGames();
        if (!currentUrlContains(EVO_ALL_GAMES)) {
            try { driver.navigate().to(EVO_ALL_GAMES); sleep(1); } catch (Exception ignore) {}
        }
    }

    private static void closeNonAllGamesAndGoAllGames() {
        Set<String> handles = driver.getWindowHandles();
        for (String h : new ArrayList<>(handles)) {
            driver.switchTo().window(h);
            String u = "";
            try { u = driver.getCurrentUrl(); } catch (Exception ignore) {}
            if (u.contains("evo-games.com") && !u.startsWith(EVO_ALL_GAMES)) driver.close();
        }
        handles = driver.getWindowHandles();
        for (String h : handles) {
            driver.switchTo().window(h);
            try {
                String u = driver.getCurrentUrl();
                if (u.startsWith(EVO_ALL_GAMES)) return;
            } catch (Exception ignore) {}
        }
    }

    private static boolean currentUrlContains(String must) {
        try { String u = driver.getCurrentUrl(); return u != null && u.startsWith(must); }
        catch (Exception e) { return false; }
    }

    private static void takeScreenshotAndSend(String uptime) {
        try {
            Path tmpDir = Paths.get(System.getProperty("java.io.tmpdir"));
            Path file = tmpDir.resolve("screenshot_" + System.currentTimeMillis() + ".png");
            File src = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            Files.copy(src.toPath(), file);
            String caption = "📸 Screenshot | ⏱ Uptime " + uptime + "\n" + nowTime();
            sendPhoto(file, caption);
            safeDelete(file);
            cleanTempScreenshots(tmpDir);
        } catch (Exception e) {
            sendTG("⚠️ Screenshot failed: " + safe(e.getMessage()));
        }
    }

    // Telegram helpers
    private static void sendTG(String text) {
        try {
            String api = "https://api.telegram.org/bot" + BOT_TOKEN + "/sendMessage";
            String payload = "chat_id=" + url(CHAT_ID) + "&text=" + url(text);
            postForm(api, payload);
        } catch (Exception ignore) {}
    }

    private static void sendPhoto(Path file, String caption) throws IOException {
        String api = "https://api.telegram.org/bot" + BOT_TOKEN + "/sendPhoto";
        Map<String,String> fields = new HashMap<>();
        fields.put("chat_id", CHAT_ID);
        fields.put("caption", caption);
        multipartFile(api, "photo", file, fields);
    }

    private static void postForm(String urlStr, String formBody) throws IOException {
        URL u = new URL(urlStr);
        HttpURLConnection c = (HttpURLConnection) u.openConnection();
        c.setRequestMethod("POST");
        c.setDoOutput(true);
        c.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        try (OutputStream os = c.getOutputStream()) { os.write(formBody.getBytes("UTF-8")); }
        readDiscard(c);
        c.disconnect();
    }

    private static void multipartFile(String urlStr, String fileField, Path file, Map<String,String> fields) throws IOException {
        String boundary = "----JavaForm" + System.currentTimeMillis();
        URL u = new URL(urlStr);
        HttpURLConnection c = (HttpURLConnection) u.openConnection();
        c.setDoOutput(true);
        c.setRequestMethod("POST");
        c.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);

        try (OutputStream out = c.getOutputStream()) {
            for (Map.Entry<String,String> e : fields.entrySet()) {
                write(out, "--" + boundary + "\r\n");
                write(out, "Content-Disposition: form-data; name=\"" + e.getKey() + "\"\r\n\r\n");
                write(out, e.getValue() + "\r\n");
            }
            String filename = file.getFileName().toString();
            write(out, "--" + boundary + "\r\n");
            write(out, "Content-Disposition: form-data; name=\"" + fileField + "\"; filename=\"" + filename + "\"\r\n");
            write(out, "Content-Type: image/png\r\n\r\n");
            try (InputStream fis = Files.newInputStream(file)) {
                byte[] buf = new byte[8192]; int n;
                while ((n = fis.read(buf)) != -1) out.write(buf, 0, n);
            }
            write(out, "\r\n--" + boundary + "--\r\n");
        }
        readDiscard(c); c.disconnect();
    }

    // Utilities
    private static void write(OutputStream os, String s) throws IOException { os.write(s.getBytes("UTF-8")); }
    private static void readDiscard(HttpURLConnection c) { try (InputStream is = c.getInputStream()) { while (is.read(new byte[2048]) != -1); } catch (Exception ignore) {} }
    private static void clickWithWait(WebDriverWait wait, By by) {
        WebElement el = wait.until(ExpectedConditions.elementToBeClickable(by));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'});", el);
        sleep(1); el.click();
    }
    private static void sleep(int sec) { try { TimeUnit.SECONDS.sleep(sec); } catch (InterruptedException ignored) {} }
    private static String uptimeString() { long e=(System.currentTimeMillis()-startMs)/1000,h=e/3600,m=(e%3600)/60,s=e%60;return String.format("%02dh %02dm %02ds",h,m,s); }
    private static String nowTime() { Calendar c=Calendar.getInstance();return String.format("%02d:%02d:%02d",c.get(Calendar.HOUR_OF_DAY),c.get(Calendar.MINUTE),c.get(Calendar.SECOND)); }
    private static void safeDelete(Path p){try{Files.deleteIfExists(p);}catch(Exception ignore){}}
    private static void cleanTempScreenshots(Path tmp){try (java.util.stream.Stream<Path> s = Files.list(tmp)) { s.filter(f->f.getFileName().toString().startsWith("screenshot_")&&f.toString().endsWith(".png")).forEach(BraveScreenshotBot::safeDelete);}catch(Exception ignore){}}
    private static String url(String s){try{return java.net.URLEncoder.encode(s,"UTF-8");}catch(Exception e){return s;}}
    private static String safe(String s){return s==null?"":s;}
}

