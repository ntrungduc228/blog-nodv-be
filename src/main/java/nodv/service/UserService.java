package nodv.service;

import nodv.model.User;

import java.util.List;

public interface UserService {
    List<User> findByEmail(String email);
}
