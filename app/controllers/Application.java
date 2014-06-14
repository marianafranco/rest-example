package controllers;

import java.util.ArrayList;
import java.util.List;

import model.Car;
import play.cache.Cache;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.index;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Application extends Controller {

    public static Result index() {
        return ok(index.render("Your new application is ready."));
    }

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

    public static Result getCar(String manufacturer, String model) {
    	List<Car> cars = (List<Car>) Cache.get("cars");
    	List<Car> result = new ArrayList<Car>();
    	for (Car car : cars) {
    		if (car.getManufacturer().equals(manufacturer)
    				&& car.getModel().equals(model)) {
    			result.add(car);
    		}
    	}
    	ObjectMapper mapper = new ObjectMapper();
    	JsonNode node = mapper.convertValue(result, JsonNode.class);
    	return ok(node);
    }
}
