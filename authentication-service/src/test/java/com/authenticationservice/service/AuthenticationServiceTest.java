package com.authenticationservice.service;

import com.authenticationservice.configuration.security.JwtConfiguration;
import com.authenticationservice.util.JwtUtil;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class AuthenticationServiceTest {

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtConfiguration jwtConfiguration;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private UserService userService;



    /*

    @Mock
    private IngredientRepository mockIngredientRepository;

    @Mock
    private PageUtil mockPageUtil;

    @Mock
    private PizzaConverter mockPizzaConverter;

    @Mock
    private PizzaRepository mockPizzaRepository;

    private PizzaService pizzaService;


    @Before
    public void init() {
        pizzaService = new PizzaService(mockIngredientRepository, mockPageUtil, mockPizzaConverter, mockPizzaRepository);
    }

     */

}
