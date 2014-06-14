package controllers;

import java.util.ArrayList;
import java.util.List;

import model.Car;
import play.cache.Cache;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.index;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Application controller.
 */
public class Application extends Controller {

    public static Result index() {
        return ok(index.render("Your new application is ready."));
    }

    /**
     * Returns all the registered cars, or the cars that match the specified
     * manufacturer or model. 
     * 
     * @param manufacturer
     * 			the car's manufacturer.
     * @param model
     * 			the car's model.
     * @return a list of cars.
     */
    public static Result getCars(String manufacturer, String model) {
    	List<Car> cars = (List<Car>) Cache.get("cars");
    	ObjectMapper mapper = new ObjectMapper();
    	JsonNode node;
    	if (manufacturer == null && model == null) {
        	node = mapper.convertValue(cars, JsonNode.class);
    	} else {
    		List<Car> result = new ArrayList<Car>();
    		for (Car car : cars) {
        		if ((manufacturer != null && car.getManufacturer().equals(manufacturer))
        				|| (model != null && car.getModel().equals(model))) {
        			result.add(car);
        		}
        	}
        	node = mapper.convertValue(result, JsonNode.class);
    	}
    	return ok(node);
    }

    /**
     * Returns a car according to the specified manufacturer and model.
     *  
     * @param manufacturer
     * 			the car's manufacturer.
     * @param model
     * 			the car's model.
     * @return the car that matches the given manufacturer and model.
     */
    public static Result getCar(String manufacturer, String model) {
    	List<Car> cars = (List<Car>) Cache.get("cars");
    	Car result = null;
    	for (Car car : cars) {
    		if (car.getManufacturer().equals(manufacturer)
    				&& car.getModel().equals(model)) {
    			result = car;
    			break;
    		}
    	}
    	if (result == null) {
    		return notFound();
    	} else {
    		ObjectMapper mapper = new ObjectMapper();
        	JsonNode node = mapper.convertValue(result, JsonNode.class);
        	return ok(node);
    	}
    }

    /**
     * Adds a new car to the cache.
     * 
     * @return HTTP 201, if the car was added to the cache. 
     * @throws JsonProcessingException
     */
    @BodyParser.Of(BodyParser.Json.class)
    public static Result addCar() throws JsonProcessingException {
    	JsonNode node = request().body().asJson();
    	ObjectMapper mapper = new ObjectMapper();
        Car newCar = mapper.treeToValue(node, Car.class);
        
        // manufacturer and model can not be empty 
        if (newCar.getManufacturer() == null || newCar.getModel() == null) {
        	return badRequest("Invalid car data.");
        }
        
        // check it a car with same manufacturer and model already exists
        List<Car> cars = (List<Car>) Cache.get("cars");
        for(Car car : cars) {
        	if(car.getManufacturer().equals(newCar.getManufacturer())
        			&& car.getModel().equals(newCar.getModel())) {
        		return status(CONFLICT);
        	}
        }
        
        cars.add(newCar);
        Cache.set("cars", cars);
    	return created();
    }

    /**
     * Updates the car that matches the specified manufacturer and model. 
     * 
     * @param manufacturer
     * 			the car's manufacturer.
     * @param model
     * 			the car's model.
     * @return HTTP 200, if the car was updated.
     * @throws JsonProcessingException
     */
    @BodyParser.Of(BodyParser.Json.class)
    public static Result updateCar(String manufacturer, String model)
    		throws JsonProcessingException {
    	JsonNode node = request().body().asJson();
    	ObjectMapper mapper = new ObjectMapper();
        Car newCar = mapper.treeToValue(node, Car.class);
        
        // manufacturer and model can not be empty
        if (newCar.getManufacturer() == null || newCar.getModel() == null) {
        	return badRequest("Invalid car data.");
        }
        
        // check if the car exists
        boolean found = false;
        List<Car> cars = (List<Car>) Cache.get("cars");
        for(Car car : cars) {
        	if(car.getManufacturer().equals(newCar.getManufacturer())
        			&& car.getModel().equals(newCar.getModel())) {
        		cars.remove(car);
        		found = true;
        		break;
        	}
        }
        
        if(!found) {
        	return notFound();
        } else {
        	cars.add(newCar);
        	Cache.set("cars", cars);
        	return ok();
        }
    }
}
