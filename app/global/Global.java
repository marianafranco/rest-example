package global;
import java.io.InputStream;
import java.util.List;

import model.Car;
import play.Application;
import play.GlobalSettings;
import play.Logger;
import play.cache.Cache;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Application global settings.
 */
public class Global extends GlobalSettings {

	private static final String CARS_FILE = "cars.json";
	
	public void onStart(Application app) {
        Logger.info("Application has started");
        try {
			loadCars();
		} catch (Exception e) {
			Logger.error("Could not load the " + CARS_FILE + " file. Error: "
					+ e.getLocalizedMessage());
		}
    }

	/**
     * Loads in the cache the cars from the cars.json file.
     * 
     * @throws Exception
     * 		If was not possible to load the cars, for whatever reason.
     */
    private void loadCars() throws Exception {
    	Logger.info("Loading " + CARS_FILE);
    	InputStream is = Global.class.getResourceAsStream(CARS_FILE);
    	ObjectMapper mapper = new ObjectMapper();
    	List<Car> cars = mapper.readValue(is, new TypeReference<List<Car>>(){});
    	Cache.set("cars", cars, 0);
    	Logger.info(cars.size() + " cars loaded.");
    }
    
    public void onStop(Application app) {
        Logger.info("Application shutdown...");
    }
}