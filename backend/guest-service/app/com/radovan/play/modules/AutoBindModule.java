package com.radovan.play.modules;

import com.google.inject.AbstractModule;
import com.radovan.play.brokers.GuestNatsListener;
import com.radovan.play.brokers.GuestNatsSender;
import com.radovan.play.config.ConsulClientRegistrationInitializer;
import com.radovan.play.converter.GuestConverter;
import com.radovan.play.repositories.GuestRepository;
import com.radovan.play.repositories.impl.GuestRepositoryImpl;
import com.radovan.play.services.*;
import com.radovan.play.services.impl.*;
import com.radovan.play.utils.JwtUtil;
import com.radovan.play.utils.NatsUtils;
import com.radovan.play.utils.PublicKeyCache;
import com.radovan.play.utils.ServiceUrlProvider;
import com.typesafe.config.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import play.Environment;

public class AutoBindModule extends AbstractModule {

    private final Environment environment;
    private final Config config;
    private static final Logger logger = LoggerFactory.getLogger(AutoBindModule.class);

    public AutoBindModule(Environment environment, Config config) {
        this.environment = environment;
        this.config = config;
    }

    @Override
    protected void configure() {
        bind(GuestService.class).to(GuestServiceImpl.class).asEagerSingleton();
        bind(GuestRepository.class).to(GuestRepositoryImpl.class).asEagerSingleton();
        bind(ConsulRegistrationService.class).to(ConsulRegistrationServiceImpl.class).asEagerSingleton();
        bind(ConsulServiceDiscovery.class).to(ConsulServiceDiscoveryImpl.class).asEagerSingleton();
        bind(ConsulClientRegistrationInitializer.class).asEagerSingleton();
        bind(ServiceUrlProvider.class).asEagerSingleton();
        bind(PublicKeyCache.class).asEagerSingleton();
        bind(JwtUtil.class).asEagerSingleton();
        bind(GuestConverter.class).asEagerSingleton();
        bind(GuestNatsSender.class).asEagerSingleton();
        bind(NatsUtils.class).asEagerSingleton();
        bind(GuestNatsListener.class).asEagerSingleton();

    }
}