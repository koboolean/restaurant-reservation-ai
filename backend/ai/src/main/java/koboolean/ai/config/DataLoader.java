package koboolean.ai.config;

import koboolean.ai.domain.TableType;
import koboolean.ai.entity.Customer;
import koboolean.ai.entity.RestaurantTable;
import koboolean.ai.repository.CustomerRepository;
import koboolean.ai.repository.RestaurantTableRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {

    private final RestaurantTableRepository tableRepository;
//    private final CustomerRepository customerRepository;

    @Override
    public void run(String... args) throws Exception {
        if(tableRepository.count() == 0){
            tableRepository.save(RestaurantTable.builder().capacity(4).type(TableType.WINDOW).build());
            tableRepository.save(RestaurantTable.builder().capacity(4).type(TableType.WINDOW).build());
            tableRepository.save(RestaurantTable.builder().capacity(4).type(TableType.HALL).build());
            tableRepository.save(RestaurantTable.builder().capacity(6).type(TableType.HALL).build());
            tableRepository.save(RestaurantTable.builder().capacity(8).type(TableType.ROOM).build());
        }

//        if(customerRepository.count() == 0){
//            customerRepository.save(Customer.builder()
//                    .name("홍길동")
//                    .phoneNumber("010-1234-5678")
//                    .visitCount(10)
//                    .memo("레드와인을 선호함")
//                    .build());
//        }

    }

}
