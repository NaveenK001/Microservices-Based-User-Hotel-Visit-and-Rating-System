package com.userservice.UserService.controller;

import com.userservice.UserService.entities.Users;
import com.userservice.UserService.service.UsersService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UsersController {

    @Autowired
    private UsersService usersService;

    private Logger logger = LoggerFactory.getLogger(UsersController.class);


    @PostMapping
    public ResponseEntity<Users> createUser(@RequestBody Users users){
        Users user1=usersService.saveUsers(users);
        return ResponseEntity.status(HttpStatus.CREATED).body(user1);

    }
   // int retryCount=1;

    @GetMapping("/{userId}")
    //@CircuitBreaker(name = "ratingHotelBreaker", fallbackMethod = "ratingHotelFallback")
    //@Retry(name = "ratingHotelService", fallba @RateLimiter(name = "userRateLimiter", fallbackMethod = "ratingHotelFallback")ckMethod = "ratingHotelFallback")
    @RateLimiter(name = "userRateLimiter", fallbackMethod = "ratingHotelFallback")
    public ResponseEntity<Users> getSingleUser(@PathVariable String userId){
        logger.info("Get Single User Handler: UserController");
     //   logger.info("Retry count: {}", retryCount);
       // retryCount++;
        Users users= usersService.getUsers(userId);
       return ResponseEntity.ok(users);

    }

    //creating fall back  method for circuitbreaker


    public ResponseEntity<Users> ratingHotelFallback(String userId, Exception ex) {
        //logger.info("Fallback is executed because service is down : ", ex.getMessage());

        ex.printStackTrace();

        Users user = Users.builder().email("dummy@gmail.com").name("Dummy").about("This user is created dummy because some service is down").userId("141234").build();
        return new ResponseEntity<>(user, HttpStatus.BAD_REQUEST);
    }

    @GetMapping
    public ResponseEntity<List<Users>> getAllUsers(){
        List<Users> allUsers=usersService.getAllUsers();
        return ResponseEntity.ok(allUsers);
    }




}
