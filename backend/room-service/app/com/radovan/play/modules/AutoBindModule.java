package com.radovan.play.modules;

import com.google.inject.AbstractModule;
import com.radovan.play.brokers.RoomNatsListener;
import com.radovan.play.brokers.RoomNatsSender;
import com.radovan.play.config.ConsulClientRegistrationInitializer;
import com.radovan.play.converter.TempConverter;
import com.radovan.play.repositories.RoomCategoryRepository;
import com.radovan.play.repositories.RoomRepository;
import com.radovan.play.repositories.impl.RoomCategoryRepositoryImpl;
import com.radovan.play.repositories.impl.RoomRepositoryImpl;
import com.radovan.play.services.ConsulServiceDiscovery;
import com.radovan.play.services.RoomCategoryService;
import com.radovan.play.services.RoomService;
import com.radovan.play.services.impl.ConsulServiceDiscoveryImpl;
import com.radovan.play.services.impl.RoomCategoryServiceImpl;
import com.radovan.play.services.impl.RoomServiceImpl;
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
        bind(RoomNatsSender.class).asEagerSingleton();
        bind(RoomService.class).to(RoomServiceImpl.class).asEagerSingleton();
        bind(RoomRepository.class).to(RoomRepositoryImpl.class).asEagerSingleton();
        bind(RoomCategoryService.class).to(RoomCategoryServiceImpl.class).asEagerSingleton();
        bind(RoomCategoryRepository.class).to(RoomCategoryRepositoryImpl.class).asEagerSingleton();
        bind(ConsulRegistrationService.class).to(ConsulRegistrationServiceImpl.class).asEagerSingleton();
        bind(ConsulServiceDiscovery.class).to(ConsulServiceDiscoveryImpl.class).asEagerSingleton();
        bind(ConsulClientRegistrationInitializer.class).asEagerSingleton();
        bind(ServiceUrlProvider.class).asEagerSingleton();
        bind(PublicKeyCache.class).asEagerSingleton();
        bind(JwtUtil.class).asEagerSingleton();
        bind(TempConverter.class).asEagerSingleton();
        bind(NatsUtils.class).asEagerSingleton();
        bind(RoomNatsListener.class).asEagerSingleton();

    }
}