package com.khouss.UsersMicroservice;

import com.khouss.UsersMicroservice.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class UsersMicroserviceApplication {


    @Autowired
    UserService userService;

	public static void main(String[] args) {
        SpringApplication.run(UsersMicroserviceApplication.class, args);
	}

    //@PostConstruct
    //public void init_users() {

        //if (roleRepository.findByRole(Messages.ROLE_USER.getText()) == null) {
        //    userService.addRole(new Role(null, Messages.ROLE_USER.getText()));
        //}
        //if (roleRepository.findByRole(Messages.ROLE_ADMIN.getText()) == null) {
        //    userService.addRole(new Role(null, Messages.ROLE_ADMIN.getText()));
        //}


        //if (userService.FindByUsername("admin") == null) {
        //    userService.saveUser(new User(null, "admin", "khouss",true, null));
        //}
        //if (userService.FindByUsername("khouss") == null) {
        //    userService.saveUser(new User(null, "khouss", "marakhib",true, null));
        //}

        //userService.addRoleToUser("admin", Messages.ROLE_ADMIN.getText());
        //userService.addRoleToUser("khouss", Messages.ROLE_USER.getText());
    //}

}
