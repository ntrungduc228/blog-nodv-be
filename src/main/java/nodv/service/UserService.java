package nodv.service;

import nodv.model.User;

import java.util.List;

public interface UserService {
    User findByEmail(String email);
}
