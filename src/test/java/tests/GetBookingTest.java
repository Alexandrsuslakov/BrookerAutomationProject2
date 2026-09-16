package tests;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import core.clients.APIClient;
import core.models.Booking;
import core.models.BookingById;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class GetBookingTest {
    private APIClient apiClient;
    private ObjectMapper objectMapper;

    // Инициализация ФЗШ клиента перед каждым тестом
    @BeforeEach
    public void setup() {
        apiClient = new APIClient();
        objectMapper = new ObjectMapper();
    }

    @Test
    public void testGetBooking() throws JsonProcessingException {
        // Выполняем Get-запрос на /booking через APIClient
        Response response = apiClient.getBooking();

        // Проверяем, что статус-код равен 200
        assertThat(response.getStatusCode()).isEqualTo(200);

        // Десериализуем тело ответа в список объектов Booking
        String responseBody = response.getBody().asString();
        List<Booking> bookings = objectMapper.readValue(responseBody, new TypeReference<List<Booking>>() {});

        // Проверяем, что тело ответа содержит объекты Booking
        assertThat(bookings).isNotEmpty(); // список не пуст

        // Проверяем, что каждый объект Booking содержит валидное значение bookingid
        for (Booking booking : bookings) {
            assertThat(booking.getBookingid()).isGreaterThan(0); // bookingid должен быть больше 0
        }
    }

    @Test
    public void testGetBookingById() throws JsonProcessingException {
        // Выполняем Get-запрос на /booking через APIClient
        Response response = apiClient.getBookingById(2);

        // Проверяем, что статус-код равен 200
        assertThat(response.getStatusCode()).isEqualTo(200);

        // Десериализуем тело ответа в список объектов
        BookingById bookingById = objectMapper.readValue(response.asString(), BookingById.class);

        // Проверяем, что тело ответа содержит правильные значения
        assertEquals("Eric", bookingById.getFirstname(), "Имя пользователя не совпадает");
        assertEquals("Wilson", bookingById.getLastname(), "Фамилия не совпадает");
        assertEquals(139, bookingById.getTotalprice(), "Неверная сумма");
        assertEquals(false, bookingById.isDepositpadid(), "Неверный статус оплаты");
        assertEquals(null, bookingById.getAdditionalneeds(), "Неверное значение доп услуг");
        assertEquals("2020-12-02", bookingById.getBookingdates().getCheckin(), "Неверная дата заезда");
        assertEquals("2022-08-03", bookingById.getBookingdates().getCheckout(), "Неверная дата выезда");
    }
}
