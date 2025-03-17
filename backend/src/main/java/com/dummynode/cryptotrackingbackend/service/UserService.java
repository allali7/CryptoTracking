/**
 * Huaihao Mo
 */
package com.dummynode.cryptotrackingbackend.service;

import com.dummynode.cryptotrackingbackend.entity.model.User;
import com.dummynode.cryptotrackingbackend.entity.model.Wallet;

import java.util.List;
import java.util.Optional;

public interface UserService {

    User createUser(User user);

    Optional<User> getUserById(String userId);

    List<User> getAllUsers();

    User updateUser(User user);

    void deleteUser(String userId);

    List<Wallet> getUserWallets(String userId);

}
