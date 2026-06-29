package koboolean.multiai.dto;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;

public record CustomerDTO(
        @JsonPropertyDescription("기존 고객 여부") boolean found,
        @JsonPropertyDescription("고객식별ID") Long id,
        @JsonPropertyDescription("핸드폰번호") String phoneNumber,
        @JsonPropertyDescription("고객성함") String name,
        @JsonPropertyDescription("방문횟수") int visitCount,
        @JsonPropertyDescription("메모") String memo
){

    public static CustomerDTO from(koboolean.multiai.entity.Customer customer){
        return new CustomerDTO(
                true,
                customer.getId(),
                customer.getPhoneNumber(),
                customer.getName(),
                customer.getVisitCount(),
                customer.getMemo()
        );
    }

    public static CustomerDTO newCustomer(String phoneNumber) {
        return new CustomerDTO(
                false,
                null,
                phoneNumber,
                null,
                0,
                null
        );
    }
}
