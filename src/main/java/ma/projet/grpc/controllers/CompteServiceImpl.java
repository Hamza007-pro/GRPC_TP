package ma.projet.grpc.controllers;


import io.grpc.stub.StreamObserver;
import ma.projet.grpc.services.CompteService;
import ma.projet.grpc.stubs.*;
import net.devh.boot.grpc.server.service.GrpcService;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@GrpcService
public class CompteServiceImpl extends CompteServiceGrpc.CompteServiceImplBase {

    private final CompteService compteService;

    public CompteServiceImpl(CompteService compteService) {
        this.compteService = compteService;
    }

    // Simuler une base de données en mémoire
    private final Map<String, Compte> compteDB = new ConcurrentHashMap<>();

    @Override
    public void allComptes(GetAllComptesRequest request, StreamObserver<GetAllComptesResponse> responseObserver) {
       var comptes = compteService.findAllComptes().stream()
                .map(compte -> Compte.newBuilder()
                        .setId(String.valueOf(compte.getId()))
                        .setSolde(compte.getSolde())
                        .setDateCreation(compte.getDateCreation())
                        .setType(TypeCompte.valueOf(compte.getTypeCompte().ordinal()))
                        .build())
                .toList();

        responseObserver.onNext(GetAllComptesResponse.newBuilder().addAllComptes(comptes).build());
        responseObserver.onCompleted();
    }

    @Override
    public void compteById(GetCompteByIdRequest request, StreamObserver<GetCompteByIdResponse> responseObserver) {
        var compte = compteService.findCompteById(request.getId());
        if (compte != null) {
            responseObserver.onNext(GetCompteByIdResponse.newBuilder()
                    .setCompte(Compte.newBuilder()
                            .setId(String.valueOf(compte.getId()))
                            .setSolde(compte.getSolde())
                            .setDateCreation(compte.getDateCreation())
                            .setType(TypeCompte.valueOf(compte.getTypeCompte().ordinal()))
                            .build())
                    .build());
        } else {
            responseObserver.onNext(GetCompteByIdResponse.newBuilder().build());
        }
        responseObserver.onCompleted();
    }



    @Override
    public void totalSolde(GetTotalSoldeRequest request, StreamObserver<GetTotalSoldeResponse> responseObserver) {
        int count = compteDB.size();
        float sum = 0;
        for (Compte compte : compteDB.values()) {
            sum += compte.getSolde();
        }
        float average = count > 0 ? sum / count : 0;

        SoldeStats stats = SoldeStats.newBuilder()
                .setCount(count)
                .setSum(sum)
                .setAverage(average)
                .build();

        responseObserver.onNext(GetTotalSoldeResponse.newBuilder().setStats(stats).build());
        responseObserver.onCompleted();
    }

    @Override
    public void saveCompte(SaveCompteRequest request, StreamObserver<SaveCompteResponse> responseObserver) {
        var compte = request.getCompte();
        var compteEntity = new  ma.projet.grpc.entities.Compte();
        compteEntity.setSolde(compte.getSolde());
        compteEntity.setDateCreation(compte.getDateCreation());
        compteEntity.setTypeCompte(ma.projet.grpc.entities.TypeCompte.valueOf(compte.getType().name()));

        var savedCompte = compteService.saveCompte(compteEntity);

        var grpcCompt = Compte.newBuilder()
                .setId(String.valueOf(savedCompte.getId()))
                .setSolde(savedCompte.getSolde())
                .setDateCreation(savedCompte.getDateCreation())
                .setType(TypeCompte.valueOf(savedCompte.getTypeCompte().ordinal()))
                .build();

        responseObserver.onNext(SaveCompteResponse.newBuilder().setCompte(grpcCompt).build());
        responseObserver.onCompleted();
    }
}