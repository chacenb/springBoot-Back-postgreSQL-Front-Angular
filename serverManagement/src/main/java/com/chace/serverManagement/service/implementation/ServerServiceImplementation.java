package com.chace.serverManagement.service.implementation;

import com.chace.serverManagement.Model.Server;
import com.chace.serverManagement.enumeration.Status;
import com.chace.serverManagement.repository.ServerRepo;
import com.chace.serverManagement.service.ServerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.net.InetAddress;
import java.util.Collection;
import java.util.Random;

import static java.lang.Boolean.TRUE;
import static org.springframework.data.domain.PageRequest.of;

// RequiredArgsConstructor annot. will create a constructor, add the serverRepo property in it
// and that will be our dependency injection
@RequiredArgsConstructor
@Service
@Transactional
@Slf4j
public class ServerServiceImplementation implements ServerService {
    private final ServerRepo serverRepo;

    /* this method is going to be called for each server save, it will:
     * - log the server to save
     * - dynamically set an image to the serv
     * - save the server to the DB and return it */
    @Override
    public Server create(Server server) {
        log.info("saving new server {}", server.getName());
        server.setImageUrl(setServerImageUrl());
        return serverRepo.save(server);
    }

    @Override
    public Server ping(String ipAddress) throws IOException {
        log.info("pinging server w/ ip : {}", ipAddress);
        Server server = serverRepo.findByIpAddress(ipAddress);
        InetAddress address = InetAddress.getByName(ipAddress);
        server.setStatus(address.isReachable(10000) ? Status.SERVER_UP : Status.SERVER_DOWN);
        serverRepo.save(server);
        return server;
    }

    @Override
    public Collection<Server> list(int limit) {
        log.info("fetching all servers ");
        return serverRepo.findAll(of(0, limit)).toList();
    }

    @Override
    public Server get(Long id) {
        log.info("fetching server w/ id : {}", id);
        return serverRepo.findById(id).get();
    }

    @Override
    public Server update(Server server) {
        log.info("updating server {}", server.getName());
        return serverRepo.save(server);
    }

    @Override
    public Boolean delete(Long id) {
        log.info("deleting server w/ id : {}", id);
        serverRepo.deleteById(id); /* if this line fails, it will throw an exception and we'll never reach the next line */
        return TRUE;
    }

    public String setServerImageUrl() {
        String[] imageNames = {"serv0.png", "serv1.png", "serv2.png", "serv3.jpg", "serv4.jpg"};
        return ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("api/v2/server/image/"+ imageNames[new Random().nextInt(5)])
                .toUriString();
    }
}
