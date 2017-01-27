package org.rutebanken.tiamat.hazelcast;

import com.hazelcast.core.*;

import org.rutebanken.hazelcasthelper.service.HazelCastService;
import org.rutebanken.hazelcasthelper.service.KubernetesService;
import org.rutebanken.tiamat.config.HazelcastConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;


import java.math.BigInteger;
import java.time.Instant;
import java.util.Collection;
import java.util.Set;

@Service
public class ExtendedHazelcastService extends HazelCastService {

    private static final Logger logger = LoggerFactory.getLogger(ExtendedHazelcastService.class);

    @Autowired
    public ExtendedHazelcastService(ExtendedKubernetesService extendedKubernetesService, HazelcastConfiguration hazelcastConfiguration) {
        super(extendedKubernetesService, hazelcastConfiguration.getHazelcastManagementUrl());
    }

    //
//
//
//    private static final String SERVER_START_TIME_KEY = "server.start.time";
//    private static final String NODE_LIVENESS_CHECK = "node.liveness.check";
//
//    public ExtendedHazelcastService(@Autowired KubernetesService kubernetesService, @Autowired HazelcastConfiguration cfg) {
//        super(kubernetesService, cfg.getHazelcastManagementUrl());
//    }
//
//    public boolean isAlive() {
//        try {
//            getLockMap().put(NODE_LIVENESS_CHECK, Instant.now());
//            return getLockMap().containsKey(NODE_LIVENESS_CHECK);
//        } catch (HazelcastInstanceNotActiveException e) {
//            logger.warn("HazelcastInstance not active - ", e);
//            return false;
//        }
//    }
//
//    @Bean
//    public Instant serverStartTime() {
//        if (!getLockMap().containsKey(SERVER_START_TIME_KEY)) {
//            getLockMap().put(SERVER_START_TIME_KEY, Instant.now());
//        }
//        return getLockMap().get(SERVER_START_TIME_KEY);
//    }
//
//    @Bean
//    public IMap<String, PtSituationElement> getSituationsMap(){
//        return hazelcast.getMap("anshar.sx");
//    }
//
//    @Bean
//    public IMap<String, Set<String>> getSituationChangesMap() {
//        return hazelcast.getMap("anshar.sx.changes");
//    }
//
//    @Bean
//    public IMap<String, EstimatedVehicleJourney> getEstimatedTimetablesMap(){
//        return hazelcast.getMap("anshar.et");
//    }
//
//    @Bean
//    public IMap<String, Set<String>> getEstimatedTimetableChangesMap() {
//        return hazelcast.getMap("anshar.et.changes");
//    }
//
//    @Bean
//    public IMap<String, VehicleActivityStructure> getVehiclesMap(){
//        return hazelcast.getMap("anshar.vm");
//    }
//
//    @Bean
//    public IMap<String, Set<String>> getVehicleChangesMap() {
//        return hazelcast.getMap("anshar.vm.changes");
//    }
//
//    @Bean
//    public IMap<String, ProductionTimetableDeliveryStructure> getProductionTimetablesMap(){
//        return hazelcast.getMap("anshar.pt");
//    }
//
//    @Bean
//    public IMap<String, Set<String>> getProductionTimetableChangesMap() {
//        return hazelcast.getMap("anshar.pt.changes");
//    }
//
//    @Bean
//    public IMap<String,SubscriptionSetup> getActiveSubscriptionsMap() {
//        return hazelcast.getMap("anshar.subscriptions.active");
//    }
//
//    @Bean
//    public IMap<String,SubscriptionSetup> getPendingSubscriptionsMap() {
//        return hazelcast.getMap("anshar.subscriptions.pending");
//    }
//
//    @Bean
//    public IMap<String, Instant> getLastActivityMap() {
//        return hazelcast.getMap("anshar.activity.last");
//    }
//
//
//    @Bean
//    public IMap<String, Instant> getLastEtUpdateRequest() {
//        return hazelcast.getMap("anshar.activity.last.et.update.request");
//    }
//
//    @Bean
//    public IMap<String, Instant> getLastPtUpdateRequest() {
//        return hazelcast.getMap("anshar.activity.last.pt.update.request");
//    }
//
//
//    @Bean
//    public IMap<String, Instant> getLastSxUpdateRequest() {
//        return hazelcast.getMap("anshar.activity.last.sx.update.request");
//    }
//
//    @Bean
//    public IMap<String, Instant> getLastVmUpdateRequest() {
//        return hazelcast.getMap("anshar.activity.last.vm.update.request");
//    }
//
//    @Bean
//    public IMap<String, Instant> getActivatedTimestampMap() {
//        return hazelcast.getMap("anshar.activity.activated");
//    }
//
//    @Bean
//    public IMap<String, Integer> getHitcountMap() {
//        return hazelcast.getMap("anshar.activity.hitcount");
//    }
//
//    @Bean
//    public IMap<String, Instant> getLockMap() {
//        return hazelcast.getMap("anshar.locks");
//    }
//
//    public String listNodes() {
//        JSONObject root = new JSONObject();
//        JSONArray clusterMembers = new JSONArray();
//        Cluster cluster = hazelcast.getCluster();
//        if (cluster != null) {
//            Set<Member> members = cluster.getMembers();
//            if (members != null && !members.isEmpty()) {
//                for (Member member : members) {
//                    JSONObject stats = new JSONObject();
//                    Collection<DistributedObject> distributedObjects = hazelcast.getDistributedObjects();
//                    for (DistributedObject distributedObject : distributedObjects) {
//                        stats.put(distributedObject.getName(), hazelcast.getMap(distributedObject.getName()).getLocalMapStats().toJson());
//                    }
//                    JSONObject obj = new JSONObject();
//                    obj.put("uuid", member.getUuid());
//                    obj.put("host", member.getAddress().getHost());
//                    obj.put("port", member.getAddress().getPort());
//                    obj.put("local", member.localMember());
//                    obj.put("localmapstats", stats);
//                    clusterMembers.add(obj);
//                }
//            }
//        }
//        root.put("members", clusterMembers);
//        return root.toString();
//    }
//
//    @Bean
//    public IMap<String, OutboundSubscriptionSetup> getOutboundSubscriptionMap() {
//        return hazelcast.getMap("anshar.subscriptions.outbound");
//    }
//
//    @Bean
//    public IMap<String, Instant> getHeartbeatTimestampMap() {
//        return hazelcast.getMap("anshar.subscriptions.outbound.heartbeat");
//    }
//
//    @Bean
//    public IMap<String,String> getStopPlaceMappings() {
//        return hazelcast.getMap("anshar.mapping.stopplaces");
//    }
//
//    @Bean
//    public IMap<String,BigInteger> getObjectCounterMap() {
//        return hazelcast.getMap("anshar.activity.objectcount");
//    }
}
