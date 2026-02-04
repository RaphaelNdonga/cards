package com.raphaelndonga.cards.controller;

import com.raphaelndonga.cards.constants.CardsConstants;
import com.raphaelndonga.cards.dto.CardsContactInfoDto;
import com.raphaelndonga.cards.dto.CardsDto;
import com.raphaelndonga.cards.dto.ResponseDto;
import com.raphaelndonga.cards.entity.Cards;
import com.raphaelndonga.cards.service.ICardsService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@Validated
public class CardsController {
    private final ICardsService iCardsService;
    private final CardsContactInfoDto cardsContactInfoDto;
    private final Environment environment;

    @Value("${build.version}")
    private String buildVersion;

    @Autowired
    public CardsController(ICardsService iCardsService, CardsContactInfoDto cardsContactInfoDto, Environment environment){
        this.iCardsService = iCardsService;
        this.cardsContactInfoDto = cardsContactInfoDto;
        this.environment = environment;
    }

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

    @GetMapping("/env-info")
    public ResponseEntity<String> getEnvInfo(){
        return ResponseEntity.status(HttpStatus.OK).body(environment.getProperty("MAVEN_HOME"));
    }

    @GetMapping("/contact-info")
    public ResponseEntity<CardsContactInfoDto> getContactInfo(){
        return ResponseEntity.status(HttpStatus.OK).body(cardsContactInfoDto);
    }

    @GetMapping("/build-info")
    public ResponseEntity<String> getBuildInfo(){
        return ResponseEntity.status(HttpStatus.OK).body(buildVersion);
    }
}
