package com.raphaelndonga.cards.controller;

import com.raphaelndonga.cards.constants.CardsConstants;
import com.raphaelndonga.cards.dto.CardsDto;
import com.raphaelndonga.cards.dto.ResponseDto;
import com.raphaelndonga.cards.entity.Cards;
import com.raphaelndonga.cards.service.ICardsService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@Validated
@AllArgsConstructor
public class CardsController {
    private ICardsService iCardsService;

    @GetMapping("/fetch")
    public ResponseEntity<CardsDto> fetchCardByMobileNumber(@RequestParam
                                                                @Pattern(regexp="(^$|[0-9]{10})",message = "Mobile number must be 10 digits")
                                                                String mobileNumber){
        CardsDto foundCard = iCardsService.fetchCard(mobileNumber);
        return ResponseEntity.status(HttpStatus.OK).body(foundCard);
    }

    @PostMapping("/create")
    public ResponseEntity<ResponseDto> createCard(@Valid @RequestParam
                                                      @Pattern(regexp="(^$|[0-9]{10})",message = "Mobile number must be 10 digits")
                                                      String mobileNumber){
        iCardsService.createCard(mobileNumber);

        return ResponseEntity.status(HttpStatus.CREATED).body(new ResponseDto(
                CardsConstants.STATUS_200, CardsConstants.MESSAGE_200
        ));
    }

    @PutMapping("/update")
    public ResponseEntity<ResponseDto> updateCard(@RequestBody CardsDto cardsDto){
        boolean isUpdated = iCardsService.updateCard(cardsDto);
        if (isUpdated){
            return ResponseEntity.status(HttpStatus.OK).body(
                    new ResponseDto(
                            CardsConstants.STATUS_200,
                            CardsConstants.MESSAGE_200
                    )
            );
        }else{
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(
                    new ResponseDto(
                            CardsConstants.STATUS_417,
                            CardsConstants.MESSAGE_417_UPDATE
                    )
            );
        }
    }

    @DeleteMapping("/delete")
    public ResponseEntity<ResponseDto> deleteCard(@RequestParam String mobileNumber){
        boolean isDeleted = iCardsService.deleteCard(mobileNumber);
        if (isDeleted){
            return ResponseEntity.status(HttpStatus.OK).body(
                    new ResponseDto(
                            CardsConstants.STATUS_200,
                            CardsConstants.MESSAGE_200
                    )
            );
        }else{
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(
                    new ResponseDto(
                            CardsConstants.STATUS_417,
                            CardsConstants.MESSAGE_417_DELETE
                    )
            );
        }
    }
}
