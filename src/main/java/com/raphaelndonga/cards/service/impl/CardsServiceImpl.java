package com.raphaelndonga.cards.service.impl;

import com.raphaelndonga.cards.constants.CardsConstants;
import com.raphaelndonga.cards.dto.CardsDto;
import com.raphaelndonga.cards.entity.Cards;
import com.raphaelndonga.cards.exception.CardsAlreadyExistsException;
import com.raphaelndonga.cards.exception.ResourceNotFoundException;
import com.raphaelndonga.cards.mapper.CardsMapper;
import com.raphaelndonga.cards.repository.CardsRepository;
import com.raphaelndonga.cards.service.ICardsService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Random;

@Service
@AllArgsConstructor
public class CardsServiceImpl implements ICardsService {
    private CardsRepository cardsRepository;

    @Override
    public void createCard(String mobileNumber) {
        Optional<Cards> foundCard = cardsRepository.findByCardNumber(mobileNumber);

        if (foundCard.isPresent()){
            throw new CardsAlreadyExistsException("The card already exists with this phone number: " + mobileNumber);
        }

        cardsRepository.save(createNewCard(mobileNumber));
    }

    private Cards createNewCard(String mobileNumber){
        Cards newCard = new Cards();
        long randomCardNumber = 100000000000L + new Random().nextInt(900000000);
        newCard.setCardNumber(Long.toString(randomCardNumber));
        newCard.setMobileNumber(mobileNumber);
        newCard.setCardType(CardsConstants.CREDIT_CARD);
        newCard.setTotalLimit(CardsConstants.NEW_CARD_LIMIT);
        newCard.setAmountUsed(0);
        newCard.setAvailableAmount(CardsConstants.NEW_CARD_LIMIT);
        return newCard;
    }

    @Override
    public CardsDto fetchCard(String mobileNumber) {
        Cards foundCard = cardsRepository.findByMobileNumber(mobileNumber).orElseThrow(()->
                new ResourceNotFoundException("Cards", "Mobile Number", mobileNumber)
        );
        return CardsMapper.mapToCardsDto(foundCard, new CardsDto());
    }

    @Override
    public boolean updateCard(CardsDto cardsDto) {
        Cards foundCard = cardsRepository.findByMobileNumber(cardsDto.getMobileNumber()).orElseThrow(()->
                new ResourceNotFoundException("Cards", "MobileNumber", cardsDto.getMobileNumber())
        );
        CardsMapper.mapToCards(cardsDto, foundCard);
        cardsRepository.save(foundCard);
        return true;
    }

    @Override
    public boolean deleteCard(String mobileNumber) {
        Cards foundCard = cardsRepository.findByMobileNumber(mobileNumber).orElseThrow(()->
                new ResourceNotFoundException("Cards", "MobileNumber", mobileNumber)
        );
        cardsRepository.deleteById(foundCard.getCardId());
        return true;
    }
}
