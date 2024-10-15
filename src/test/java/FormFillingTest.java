import com.github.javafaker.Faker;
import enam.FormField;
import enam.ResultField;
import factory.WebDriverFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FormFillingTest {
    private static final Faker faker = new Faker();
    private final Logger logger = LogManager.getLogger(FormFillingTest.class);
    private final String baseUrl = System.getProperty("baseUrl");
    private final String pathDz = ".home.kartushin.su/form.html";
    private WebDriver driver;
    private final WebDriverFactory webDriverFactory = new WebDriverFactory();
    private WebDriverWait wait;

    @BeforeEach
    public void setUp() {
        String browserType = System.getProperty("browser", "chrome"); // Теперь по умолчанию chrome
        driver = webDriverFactory.getDriver(browserType); // Передаем browserType
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    @AfterEach
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    public void testFormFilling() {
        driver.get(baseUrl + pathDz);

        // Генерация данных
        String usernameValue = faker.name().username();
        String emailValue = faker.internet().emailAddress();
        String passwordValue = faker.internet().password();
        String confirmPasswordValue = passwordValue;

        logger.info("Сгенерировано имя пользователя: {}", usernameValue);
        logger.info("Сгенерированный email: {}", emailValue);
        logger.info("Сгенерированный пароль: {}", passwordValue);

        // Генерация даты рождения
        Date birthdate = faker.date().birthday();
        String inputBirthdateValue = new SimpleDateFormat("dd-MM-yyyy").format(birthdate); // Формат для ввода
        String expectedBirthdateValue = new SimpleDateFormat("yyyy-MM-dd").format(birthdate); // Ожидаемый формат для проверки

        // Заполнение формы
        fillField(FormField.USERNAME, usernameValue);
        fillField(FormField.EMAIL, emailValue);
        fillField(FormField.PASSWORD, passwordValue);
        fillField(FormField.CONFIRM_PASSWORD, confirmPasswordValue);
        fillField(FormField.BIRTHDATE, inputBirthdateValue);

        // Заполнение поля уровня языка
        Select languageSelect = new Select(driver.findElement(By.id(FormField.LANGUAGE_LEVEL.getFieldName())));
        languageSelect.selectByVisibleText("Начальный"); // Выбор языка
        String expectedLanguageLevel = "beginner"; // Ожидаемое значение

        if (passwordValue.equals(confirmPasswordValue)) {
            logger.info("Пароль и подтверждение пароля совпадают.");
        } else {
            logger.warn("Пароль и подтверждение пароля не совпадают!");
        }

        // Нажимаем кнопку отправки
        driver.findElement(By.cssSelector("input[type='submit'][value='Зарегистрироваться']")).click();

        // Ожидание загрузки следующей страницы или элемента

        logger.info("Кнопка 'Зарегистрироваться' нажата.");
        // Проверка выводимых данных

        checkResultField(ResultField.USERNAME, usernameValue);
        checkResultField(ResultField.EMAIL, emailValue);
        checkResultField(ResultField.BIRTHDATE, expectedBirthdateValue);
        checkResultField(ResultField.LANGUAGE_LEVEL, expectedLanguageLevel);
        logger.info("Данные отображаются корректно ");
    }

    private void fillField(FormField field, String value) {
        WebElement inputField = driver.findElement(By.id(field.getFieldName()));
        inputField.clear(); // Чистим поле перед вводом
        inputField.sendKeys(value); // Вводим значение
        logger.info("Поле '{}' заполнено значением: {}", field.getFieldName(), value);
    }


    private void checkResultField(ResultField field, String expectedValue) {
        // Получаем текст из элемента вывода
        WebElement outputDiv = driver.findElement(By.id("output"));
        String outputText = outputDiv.getText();
        // Извлекаем значение, используя ключ
        String actualValue = extractValueFromOutput(outputText, field.getDisplayName());
        // Сравнение значений с ожиданиями
        logger.info("Проверка значения для поля '{}'. Ожидаемое: '{}', фактическое: '{}'", field.getDisplayName(), expectedValue, actualValue);

        assertEquals(expectedValue, actualValue, "Выведенное значение " + field.getDisplayName() + " не совпадает с ожидаемым.");
    }

    private String extractValueFromOutput(String outputText, String key) {
        // Разбиваем текст по строкам и ищем ключ
        for (String line : outputText.split("\n")) {
            if (line.contains(key)) {
                return line.substring(line.indexOf(":") + 1).trim(); // Возвращаем значение после двоеточия
            }
        }
        logger.warn("Ключ '{}' не найден в выводе.", key);
        return ""; // Возвращаем пустую строку, если не найдено
    }
}
