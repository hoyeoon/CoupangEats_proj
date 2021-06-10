package com.example.demo.src.main;

import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MainService {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final MainDao mainDao;
    private final MainProvider mainProvider;
    private final JwtService jwtService;

    @Autowired
    public MainService(MainDao mainDao, MainProvider mainProvider, JwtService jwtService) {
        this.mainDao = mainDao;
        this.mainProvider = mainProvider;
        this.jwtService = jwtService;
    }
}