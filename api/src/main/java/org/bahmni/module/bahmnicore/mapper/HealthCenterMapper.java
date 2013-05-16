package org.bahmni.module.bahmnicore.mapper;

import org.bahmni.module.bahmnicore.model.BahmniPatient;
import org.openmrs.*;
import org.openmrs.api.LocationService;
import org.openmrs.api.context.Context;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;

@Component
public class HealthCenterMapper {
	
	public Patient map(Patient person, BahmniPatient bahmniPatient) {
		LocationService locationService = Context.getLocationService();
		List<Location> allLocations = locationService.getAllLocations();
		String center = bahmniPatient.getCenterName();
		
		List<LocationAttributeType> allLocationAttributeTypes = locationService.getAllLocationAttributeTypes();
		LocationAttributeType identifierSourceName = findIdentifierSourceName(allLocationAttributeTypes);
		
		for (Location location : allLocations) {
			Collection<LocationAttribute> activeAttributes = location.getActiveAttributes();
			for (LocationAttribute attribute : activeAttributes) {
				addHealthCenter(person, center, identifierSourceName, location, attribute);
			}
		}
        return person;
	}
	
	private LocationAttributeType findIdentifierSourceName(List<LocationAttributeType> allLocationAttributeTypes) {
		LocationAttributeType identifierSourceName = null;
		for (LocationAttributeType attributeType : allLocationAttributeTypes) {
			if (attributeType.getName().equals("IdentifierSourceName")) {
				identifierSourceName = attributeType;
				break;
			}
		}
		return identifierSourceName;
	}
	
	private void addHealthCenter(Person person, String center, LocationAttributeType identifierSourceName,
	        Location location, LocationAttribute attribute) {
		if (attribute.getAttributeType().equals(identifierSourceName) && attribute.getValue().toString().equals(center)) {
			PersonAttribute locationAttribute = new PersonAttribute();
			locationAttribute.setAttributeType(Context.getPersonService().getPersonAttributeTypeByName("Health Center"));
			locationAttribute.setValue(location.getId().toString());
			person.addAttribute(locationAttribute);
		}
	}
}
