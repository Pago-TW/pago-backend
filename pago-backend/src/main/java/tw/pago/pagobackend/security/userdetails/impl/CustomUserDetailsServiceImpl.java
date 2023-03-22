package tw.pago.pagobackend.security.userdetails.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tw.pago.pagobackend.dao.UserDao;
import tw.pago.pagobackend.exception.ResourceNotFoundException;
import tw.pago.pagobackend.model.User;
import tw.pago.pagobackend.security.model.UserPrincipal;
import tw.pago.pagobackend.security.userdetails.CustomUserDetails;


@Service
public class CustomUserDetailsServiceImpl implements UserDetailsService {

  @Autowired
  private UserDao userDao;


  @Override
  @Transactional
  public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

    User user = userDao.getUserByEmail(email);

    if (user == null) {
      throw new UsernameNotFoundException("Could not find user");
    }

    return UserPrincipal.create(user);
  }

  @Transactional
  public UserDetails loadUserById(String userId) {
    User user = userDao.getUserById(userId);
    if (user == null) {
      throw new ResourceNotFoundException("User", "id", userId);
    }

    return UserPrincipal.create(user);
  }


}
