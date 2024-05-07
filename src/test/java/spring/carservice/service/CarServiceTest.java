package spring.carservice.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import spring.carservice.model.Car;
import spring.carservice.model.Color;
import spring.carservice.repository.CarRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CarServiceTest {
    @Mock
    private CarRepository carRepository;
    @InjectMocks
    private CarService carService;

    @BeforeEach
    void setUp() {
        when(carRepository.getAllCars()).thenReturn(getAllCars());
    }

    public Car carId1 = Car.builder().id(1).mark("Ford").model("Mustang").color(Color.BLACK).build();
    public Car carId2 = Car.builder().id(2).mark("Ford").model("Mustang").color(Color.BLUE).build();
    public Car carId3 = Car.builder().id(3).mark("Mazda").model("CX30").color(Color.RED).build();
    public Car carId4 = Car.builder().id(4).mark("Nissan").model("Note").color(Color.YELLOW).build();

    @Test
    void should_return_all_cars_from_repository() {
        final List<Car> allCars = carService.getAllCars();
        allCars.forEach(System.out::println); // chwilowo do sprawdzenia czy korzysta z MOCKA dla repo
        verify(carRepository).getAllCars(); // sprawdzamy czy metoda 'carService.getAllCars()' wywołała carRepository.getAllCars()
        assertThat(allCars)
                .isNotEmpty()
                .hasSize(4);
    }

    @Test
    void should_find_car_by_id() {
        final Optional<Car> carById = carService.getCarById(1);

        assertThat(carById)
                .isNotEmpty()
                .isEqualTo(Optional.of(carId1));
    }

    @Test
    void should_return_optional_empty_when_car_was_not_found_by_id() {
        final Optional<Car> carById = carService.getCarById(10);
        assertThat(carById)
                .isEmpty();
    }

    @Test
    void should_find_cars_by_color() {
        final List<Car> carsByColor = carService.getCarsByColor(Color.RED);
        assertThat(carsByColor)
                .isNotEmpty()
                .hasSize(1);
    }

    @Test
    void should_find_car_by_color() {
        final List<Car> carsByColor = carService.getCarsByColor(Color.YELLOW);
        assertThat(carsByColor)
                .isNotEmpty()
                .hasSize(1)
                .containsExactly(Car.builder().id(4).mark("Nissan").model("Note").color(Color.YELLOW).build())
        ;
    }

    @Test
    void should_return_empty_list_when_car_was_not_found_by_color() {
        List<Car> carsByColor = carService.getCarsByColor(Color.WHITE);
        assertThat(carsByColor)
                .isEmpty();
    }

    @Test
    void should_add_car_to_database_when_car_with_same_id_not_present_in_database() {
        // jaki pojazd chcesz dodać ?
        Car carToAdd = Car.builder().id(5).mark("Hunday").model("Multipla").color(Color.RED).build();
        // jaka metoda z carService wywołuje metode w repozytorium do dodawania pojazdów
        Optional<Car> car = carService.addCar(carToAdd);
        // sprawdz czy użyłeś metody z repozytorium po wywołaniu serwisu
        verify(carRepository).addCar(carToAdd);
        // sprawdz czy dodano pojazd
        assertThat(car).isNotEmpty();
        assertThat(car.get()).isEqualTo(carToAdd);
    }

    @Test
    void should_add_car_when_car_with_same_id_not_present_id_database() {
        final Car carToAdd = Car.builder().id(5).mark("Nissan").model("Mikra").color(Color.BLACK).build();
        Car car = carService.addOrUpdateCar(5, carToAdd);
        verify(carRepository).addCar(carToAdd);
        assertThat(car).isEqualTo(carToAdd);
    }

    @Test
    void should_update_full_car_when_car_with_same_id_existing_in_database() {
        Car carToAdd = Car.builder().id(1).mark("Kia").model("Ceed").color(Color.GREEN).build();

        carService.addOrUpdateCar(1, carToAdd);
        verify(carRepository).updateFullCar(carId1, carToAdd);
        // ponieważ metoda 'updateFullCar' nie możemy sprawdzić, czy obiekty są identyczne
    }

    @Test
    void should_delete_car_by_id_when_car_find_in_database() {
        Optional<Car> deletedCar = carService.deleteCarById(1);
        verify(carRepository).deleteCar(carId1);
        assertThat(deletedCar).isNotEmpty();
        assertThat(deletedCar.get()).isEqualTo(carId1);
    }

    @Test
    void should_return_empty_optional_when_car_not_found_in_database() {
        Optional<Car> deletedCar = carService.deleteCarById(10);
        verify(carRepository).getAllCars();
        verifyNoMoreInteractions(carRepository);
        assertThat(deletedCar).isEmpty();
    }


    private List<Car> getAllCars() {
        return Arrays.asList(
                // użycie wzorca budowniczy do stworzenia pojazdów
                carId1,carId2,carId3,carId4
        );
    }
}