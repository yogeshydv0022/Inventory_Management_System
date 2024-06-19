package com.files.securityConfig;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.files.jwt.JwtAuthFilter;
import com.files.securityService.UserDetailsServiceImpl;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

	@Autowired
	private JwtAuthFilter jwtAuthFilter;

//	@Autowired
//	private UserRepository userRepository;

	@Bean
	public UserDetailsService userDetailsService() {
		return new UserDetailsServiceImpl();
	}

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		return http.cors(c -> c.disable()).csrf(crsf -> crsf.disable())
				.authorizeHttpRequests(auth -> auth
						.requestMatchers("/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**")
						.permitAll()
						.requestMatchers(HttpMethod.POST, "/ims/auth/**", "/ims/users/**", "/ims/superAdmin/**", "/ims/admin/**")
						.permitAll()
						// .requestMatchers(HttpMethod.POST, "/admin/**").permitAll()
						.requestMatchers(HttpMethod.GET, "/ims/bills/**","/ims/cart/**","/ims/pdf/**").hasRole("ADMIN")
//						.requestMatchers(HttpMethod.PUT, "/ims/admin/**").permitAll()
//					.requestMatchers(HttpMethod.PUT, "/users/**","/category/**","/books/**").hasRole("ADMIN").anyRequest().authenticated()
//				.requestMatchers(HttpMethod.DELETE, "/ims/users/**").hasRole("ADMIN")
				.anyRequest().authenticated())
				.authenticationProvider(authenticationProvider())
				.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
//        Add JWT token filter
				.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class).build();

	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public AuthenticationProvider authenticationProvider() {
		DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
		authenticationProvider.setUserDetailsService(userDetailsService());
		authenticationProvider.setPasswordEncoder(passwordEncoder());
		return authenticationProvider;

	}

	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
		return config.getAuthenticationManager();
	}

	@Bean
	CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration configuration = new CorsConfiguration();

		configuration.setAllowedOrigins(List.of("http://localhost:4200"));
		configuration.setAllowedMethods(List.of("GET", "POST","PUT", "DELETE"));
		configuration.setAllowedHeaders(List.of("Authorization", "Content-Type"));

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

		source.registerCorsConfiguration("/**", configuration);

		return (CorsConfigurationSource) source;
	}
}