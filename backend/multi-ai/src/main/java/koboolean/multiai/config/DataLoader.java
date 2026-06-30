package koboolean.multiai.config;

import koboolean.multiai.domain.TableType;
import koboolean.multiai.entity.Customer;
import koboolean.multiai.entity.Menu;
import koboolean.multiai.entity.RestaurantTable;
import koboolean.multiai.repo.CustomerRepository;
import koboolean.multiai.repo.MenuRepository;
import koboolean.multiai.repo.RestaurantTableRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {

    private final RestaurantTableRepository tableRepository;
    private final CustomerRepository customerRepository;
    private final MenuRepository menuRepository;

    @Override
    public void run(String... args) throws Exception {
        if(tableRepository.count() == 0){
            tableRepository.save(RestaurantTable.builder().capacity(4).type(TableType.WINDOW).build());
            tableRepository.save(RestaurantTable.builder().capacity(4).type(TableType.WINDOW).build());
            tableRepository.save(RestaurantTable.builder().capacity(4).type(TableType.HALL).build());
            tableRepository.save(RestaurantTable.builder().capacity(6).type(TableType.HALL).build());
            tableRepository.save(RestaurantTable.builder().capacity(8).type(TableType.ROOM).build());
        }

        if(customerRepository.count() == 0){
            customerRepository.save(Customer.builder()
                    .name("홍길동")
                    .phoneNumber("010-1234-5678")
                    .visitCount(10)
                    .memo("레드와인을 선호함")
                    .build());
        }

        if(menuRepository.count() == 0){
            menuRepository.save(Menu.builder().name("티본 스테이크").price(25000).category("MAIN").build());
            menuRepository.save(Menu.builder().name("봉골레 파스타").price(15000).category("MAIN").build());
            menuRepository.save(Menu.builder().name("트러플 리조또").price(18000).category("MAIN").build());
            menuRepository.save(Menu.builder().name("카베르네 쇼비뇽").price(55000).category("WINE").build());
            menuRepository.save(Menu.builder().name("샴페인").price(40000).category("WINE").build());
            menuRepository.save(Menu.builder().name("수제 티라미수").price(1200).category("DESSERT").build());
        }

    }

}
