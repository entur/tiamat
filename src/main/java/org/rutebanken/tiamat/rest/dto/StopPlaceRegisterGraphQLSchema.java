package org.rutebanken.tiamat.rest.dto;

import static graphql.Scalars.GraphQLBigDecimal;
import static graphql.Scalars.GraphQLBoolean;
import static graphql.Scalars.GraphQLLong;
import static graphql.Scalars.GraphQLString;
import static graphql.schema.GraphQLFieldDefinition.newFieldDefinition;
import static graphql.schema.GraphQLObjectType.newObject;

import org.rutebanken.tiamat.model.Site_VersionStructure;
import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.model.StopTypeEnumeration;
import org.rutebanken.tiamat.repository.StopPlaceRepository;

import graphql.Scalars;
import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import graphql.schema.GraphQLArgument;
import graphql.schema.GraphQLEnumType;
import graphql.schema.GraphQLList;
import graphql.schema.GraphQLObjectType;
import graphql.schema.GraphQLSchema;

public class StopPlaceRegisterGraphQLSchema {

    public GraphQLSchema stopPlaceRegisterSchema;

    public StopPlaceRegisterGraphQLSchema(StopPlaceRepository stopPlaceRepository) {
		super();
		
	    DataFetcher stopPlaceFetcher = new DataFetcher() {
	        @Override
			public
	        Object get(DataFetchingEnvironment environment) {
	            Long id = environment.getArgument("id"); 
	        	if(id != null) {
	        		return new Object[] {stopPlaceRepository.findOne(id)};
	        	} else {
	        		Iterable<StopPlace> findAll = stopPlaceRepository.findAll();
	        		return findAll;
	        	}
	        }};

        
	          GraphQLEnumType stopPlaceTypeEnum = GraphQLEnumType.newEnum()
	                .name("stopPlaceType")
	                
	                .value("onstreetBus",StopTypeEnumeration.ONSTREET_BUS)
	                .value("onstreetTram",StopTypeEnumeration.ONSTREET_TRAM)
	                .value("airport",StopTypeEnumeration.AIRPORT)
	                .value("railStation",StopTypeEnumeration.RAIL_STATION)
	                .value("metroStation",StopTypeEnumeration.METRO_STATION)
	                .value("busStation",StopTypeEnumeration.BUS_STATION)
	                .value("coachStation",StopTypeEnumeration.COACH_STATION)
	                .value("tramStation",StopTypeEnumeration.TRAM_STATION)
	                .value("harbourPort",StopTypeEnumeration.HARBOUR_PORT)
	                .value("ferryPort",StopTypeEnumeration.FERRY_PORT)
	                .value("ferryStop",StopTypeEnumeration.FERRY_STOP)
	                .value("liftStation",StopTypeEnumeration.LIFT_STATION)
	                .value("vehicleRailInterchange",StopTypeEnumeration.VEHICLE_RAIL_INTERCHANGE)
	                .value("other",StopTypeEnumeration.OTHER)
	                .build();
	        
		    GraphQLObjectType location = newObject()
		            .name("location")
		            .field(newFieldDefinition()
		                    .type(GraphQLBigDecimal)
		                    .name("longitude"))
		            .field(newFieldDefinition()
		                    .type(GraphQLBigDecimal)
		                    .name("latitude"))
		            .build();

		    GraphQLObjectType centroid = newObject()
		            .name("centroid")
		            .field(newFieldDefinition()
		                    .type(location)
		                    .name("location"))
		            .build();

		    GraphQLObjectType multiLingualString = newObject()
		            .name("multiLingualString")
		            .field(newFieldDefinition()
		                    .type(GraphQLString)
		                    .name("lang"))
		            .field(newFieldDefinition()
		                    .type(GraphQLString)
		                    .name("value"))
		            .build();

		    GraphQLObjectType quayType = newObject()
		            .name("quay")
		            .field(newFieldDefinition()
		                    .type(GraphQLLong)
		                    .name("id"))
		            .field(newFieldDefinition()
		                    .type(multiLingualString)
		                    .name("name"))
		            .field(newFieldDefinition()
		                    .type(GraphQLBoolean)
		                    .name("allAreasWheelchairAccessible"))
		            .field(newFieldDefinition()
		                    .type(centroid)
		                    .name("centroid"))
		            .build();

		    GraphQLObjectType stopPlaceType = newObject()
		            .name("stopPlace")
		            .field(newFieldDefinition()
		                    .type(GraphQLLong)
		                    .name("id"))
		            .field(newFieldDefinition()
		                    .type(multiLingualString)
		                    .name("name"))
//		            .field(newFieldDefinition()
//		                    .type(GraphQLString)
//		                    .name("parentSiteRef")
//		                    .dataFetcher(e -> ((StopPlace)e.getSource()).getParentSiteRef().getValue())) // TODO solve recursive parents
		            .field(newFieldDefinition()
		                    .type(GraphQLBoolean)
		                    .name("allAreasWheelchairAccessible"))
		            .field(newFieldDefinition()
		                    .type(stopPlaceTypeEnum)
		                    .name("stopPlaceType"))
		            .field(newFieldDefinition()
		                    .type(centroid)
		                    .name("centroid"))
		            .field(newFieldDefinition()
		                    .type(new GraphQLList(quayType))
		                    .name("quays"))

		            .build();
	        
		    
	        
	        
	    GraphQLObjectType queryType = newObject()
	            .name("findStopPlaces")
	            .field(newFieldDefinition()
	                    .type(new GraphQLList(stopPlaceType))
	                    .name("stopPlace")
	                    .argument(GraphQLArgument.newArgument()
	                            .name("id")
	                            .type(Scalars.GraphQLLong))
//	                    .argument(GraphQLArgument.newArgument()
//	                    		.name("stopPlaceType")
//	                    		.type(stopPlaceTypeEnum))
	                    .dataFetcher(stopPlaceFetcher)
	                    )
	            
	            .build();
	    
	    stopPlaceRegisterSchema = GraphQLSchema.newSchema()
	            .query(queryType)
	            .build();
	}

}
