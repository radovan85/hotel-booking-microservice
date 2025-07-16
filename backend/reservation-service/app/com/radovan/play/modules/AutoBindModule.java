package com.radovan.play.modules;

import com.google.inject.AbstractModule;
import com.radovan.play.brokers.ReservationNatsListener;
import com.radovan.play.brokers.ReservationNatsSender;
import com.radovan.play.config.ConsulClientRegistrationInitializer;
import com.radovan.play.converter.TempConverter;
import com.radovan.play.repositories.NoteRepository;
import com.radovan.play.repositories.ReservationRepository;
import com.radovan.play.repositories.impl.NoteRepositoryImpl;
import com.radovan.play.repositories.impl.ReservationRepositoryImpl;
import com.radovan.play.services.ConsulRegistrationService;
import com.radovan.play.services.ConsulServiceDiscovery;
import com.radovan.play.services.NoteService;
import com.radovan.play.services.ReservationService;
import com.radovan.play.services.impl.ConsulRegistrationServiceImpl;
import com.radovan.play.services.impl.ConsulServiceDiscoveryImpl;
import com.radovan.play.services.impl.NoteServiceImpl;
import com.radovan.play.services.impl.ReservationServiceImpl;
import com.radovan.play.utils.*;
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
        bind(NoteService.class).to(NoteServiceImpl.class).asEagerSingleton();
        bind(NoteRepository.class).to(NoteRepositoryImpl.class).asEagerSingleton();
        bind(ReservationService.class).to(ReservationServiceImpl.class).asEagerSingleton();
        bind(ReservationRepository.class).to(ReservationRepositoryImpl.class).asEagerSingleton();
        bind(ConsulRegistrationService.class).to(ConsulRegistrationServiceImpl.class).asEagerSingleton();
        bind(ConsulServiceDiscovery.class).to(ConsulServiceDiscoveryImpl.class).asEagerSingleton();
        bind(ConsulClientRegistrationInitializer.class).asEagerSingleton();
        bind(ServiceUrlProvider.class).asEagerSingleton();
        bind(PublicKeyCache.class).asEagerSingleton();
        bind(JwtUtil.class).asEagerSingleton();
        bind(TempConverter.class).asEagerSingleton();
        bind(NatsUtils.class).asEagerSingleton();
        bind(TimeConversionUtils.class).asEagerSingleton();
        bind(ReservationNatsSender.class).asEagerSingleton();
        bind(ReservationNatsListener.class).asEagerSingleton();
    }
}