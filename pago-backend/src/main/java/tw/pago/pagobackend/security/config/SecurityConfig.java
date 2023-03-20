package tw.pago.pagobackend.security.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import tw.pago.pagobackend.security.oauth2.OAuth2AuthenticationSuccessHandler;
import tw.pago.pagobackend.service.UserService;
import tw.pago.pagobackend.service.impl.CustomOAuth2UserServiceImpl;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

  private final CustomOAuth2UserServiceImpl customOAuth2UserService;
  private final UserService userService;

  @Autowired
  public SecurityConfig(CustomOAuth2UserServiceImpl customOAuth2UserService, UserService userService) {
    this.customOAuth2UserService = customOAuth2UserService;
    this.userService = userService;
  }


  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http.csrf().disable()
        .authorizeRequests()
        .antMatchers("/login", "/oauth/**", "/orders/**").permitAll()
        .anyRequest().authenticated()
        .and()
        .formLogin().permitAll()
        .and()
        .oauth2Login()
        .loginPage("/login")
        .userInfoEndpoint()
        .userService(customOAuth2UserService)
        .and()
        .successHandler(new OAuth2AuthenticationSuccessHandler(userService));
  }
}
