package tw.pago.pagobackend.security.userdetails.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import tw.pago.pagobackend.dao.UserDao;
import tw.pago.pagobackend.model.User;
import tw.pago.pagobackend.security.userdetails.CustomUserDetails;


@Service
public class UserDetailsServiceImpl implements UserDetailsService {

  @Autowired
  private UserDao userDao;


  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

    User user = userDao.getUserByEmail(username);

    if (user == null) {
      throw new UsernameNotFoundException("Could not find user");
    }

    return new CustomUserDetails(user);
  }
}
