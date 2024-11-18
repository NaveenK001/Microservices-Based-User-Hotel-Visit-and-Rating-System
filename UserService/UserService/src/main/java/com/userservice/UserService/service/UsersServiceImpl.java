package com.userservice.UserService.service;

import com.userservice.UserService.entities.Hotel;
import com.userservice.UserService.entities.Rating;
import com.userservice.UserService.entities.Users;
import com.userservice.UserService.exception.ResourceNotFoundException;
import com.userservice.UserService.repository.UsersRepository;

import com.userservice.UserService.serviceExternal.HotelService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;


@Service
public class UsersServiceImpl implements UsersService{

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private HotelService hotelService;


    private final Logger logger= LoggerFactory.getLogger(UsersServiceImpl.class);

    @Override
    public Users saveUsers(Users users) {
        String randomUserId=UUID.randomUUID().toString();
        users.setUserId(randomUserId);
       return usersRepository.save(users);
    }

    @Override
    public List<Users> getAllUsers() {
        return usersRepository.findAll();
    }

    @Override
    public Users getUsers(String userId) {
        //get user from database with the help of user repository
        Users users= usersRepository.findById(userId).orElseThrow(()-> new ResourceNotFoundException("User with given Id is not found on server!!:"+userId));
        //fetch rating of the above user from RATING SERVICE
        //http://localhost:8093/ratings/users/1fda1ea7-e3f5-4d8e-8f3c-f1061f2e0272
        Rating[] ratingsOfUser=restTemplate.getForObject("http://RATINGSERVICE/ratings/users/"+users.getUserId(), Rating[].class);
        logger.info("{}", ratingsOfUser);

        List<Rating> ratings= Arrays.stream(ratingsOfUser).toList();

        List<Rating> ratingList=ratings.stream().map(rating -> {
            //api call to hotel service to get the hotel

 //http://localhost:8092/hotels/f095b65b-2531-4e46-8ca5-6e4da6d1b55a
            //ResponseEntity<Hotel> forEntity = restTemplate.getForEntity("http://HOTELSERVICE/hotels/"+rating.getHotelId(), Hotel.class);
            //set the hotel to rating
            Hotel hotel=hotelService.getHotel(rating.getHotelId());
           //logger.info("response status code: {} ",forEntity.getStatusCode());

            //set the hotel to rating
            rating.setHotel(hotel);
            //return the rating
            return rating;

        }).collect(Collectors.toList());
        users.setRatings(ratingList);

        return users;
    }
}
