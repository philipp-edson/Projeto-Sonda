package br.sci.appsondavalidation.model.entity;

import java.util.Date;

/**
 * 
 * @version 1.0.0
 * @author Rafael Carvalho Chagas
 * @since Copyright 2012-2013
 *
 */

public class InfoData {
	private String id;
	private String inputData;
	private String outputData;
	private String outputCode;
	private String outputReport;
	private String observation;
	private String station;
	private String month;
	private String year;
			
	private Date dateOfValidation;
	
	private double latitudeOfStation;
	private double longitudeOfStation;
		
	
	public InfoData() {
		super();
	}
	
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public String getInputData() {
		return inputData;
	}
	
	public void setInputData(String inputData) {
		this.inputData = inputData;
	}
	
	public String getOutputData() {
		return outputData;
	}
	
	public void setOutputData(String outputData) {
		this.outputData = outputData;
	}
	
	public String getOutputCode() {
		return outputCode;
	}
	
	public void setOutputCode(String outputCode) {
		this.outputCode = outputCode;
	}
	
	public String getOutputReport() {
		return outputReport;
	}
	
	public void setOutputReport(String outputReport) {
		this.outputReport = outputReport;
	}
	
	public String getStation() {
		return station;
	}
	
	public void setStation(String station) {
		this.station = station;
	}
	
	public String getObservation() {
		return observation;
	}
	
	public void setObservation(String observation) {
		this.observation = observation;
	}
	
	public Date getDateOfValidation() {
		return dateOfValidation;
	}
	
	public void setDateOfValidation(Date dateOfValidation) {
		this.dateOfValidation = dateOfValidation;
	}
	
	public double getLatitudeOfStation() {
		return latitudeOfStation;
	}
	
	public void setLatitudeOfStation(double latitudeOfStation) {
		this.latitudeOfStation = latitudeOfStation;
	}
	
	public double getLongitudeOfStation() {
		return longitudeOfStation;
	}
	
	public void setLongitudeOfStation(double longitudeOfStation) {
		this.longitudeOfStation = longitudeOfStation;
	}
	
	public String getMonth() {
		return month;
	}
	
	public void setMonth(String month) {
		this.month = month;
	}
	
	public String getYear() {
		return year;
	}
	
	public void setYear(String year) {
		this.year = year;
	}
		
	@Override
	public int hashCode() {
		final int PRIME = 31;
		int RESULT = 1;
		long temp1, temp2;
		
		temp1 = Double.doubleToLongBits(latitudeOfStation);
		temp2 = Double.doubleToLongBits(longitudeOfStation);
		
		RESULT = PRIME * RESULT + ((id == null) ? 0 : id.hashCode());
		RESULT = PRIME * RESULT + ((inputData == null) ? 0 : inputData.hashCode());
		RESULT = PRIME * RESULT + ((outputData == null) ? 0 : outputData.hashCode());
		RESULT = PRIME * RESULT + ((outputCode == null) ? 0 : outputCode.hashCode());
		RESULT = PRIME * RESULT + ((outputReport == null) ? 0 : outputReport.hashCode());
		RESULT = PRIME * RESULT + ((observation == null) ? 0 : observation.hashCode());
		RESULT = PRIME * RESULT + ((station == null) ? 0 : station.hashCode());
		RESULT = PRIME * RESULT + ((dateOfValidation == null) ? 0 : dateOfValidation.hashCode());
		RESULT = PRIME * RESULT + (int) (temp1 ^ (temp1 >>> 32));
		RESULT = PRIME * RESULT + (int) (temp2 ^ (temp2 >>> 32));
		RESULT = PRIME * RESULT + ((month == null) ? 0 : month.hashCode());
		RESULT = PRIME * RESULT + ((year == null) ? 0 : year.hashCode());
		return RESULT;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		InfoData other = (InfoData) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;			
		if (inputData == null) {
			if (other.inputData != null)
				return false;
		} else if (!inputData.equals(other.inputData))
			return false;
		if (outputData == null) {
			if (other.outputData != null)
				return false;
		} else if (!outputData.equals(other.outputData))
			return false;
		if (outputCode == null) {
			if (other.outputCode != null)
				return false;
		} else if (!outputCode.equals(other.outputCode))
			return false;
		if (outputReport == null) {
			if (other.outputReport != null)
				return false;
		} else if (!outputReport.equals(other.outputReport))
			return false;
		if (station == null) {
			if (other.station != null)
				return false;
		} else if (!station.equals(other.station))
			return false;
		if (observation == null) {
			if (other.observation != null)
				return false;
		} else if (!observation.equals(other.observation))
			return false;
		if (dateOfValidation == null) {
			if (other.dateOfValidation != null)
				return false;
		} else if (!dateOfValidation.equals(other.dateOfValidation))
			return false;
		if (Double.doubleToLongBits(latitudeOfStation) != Double
				  .doubleToLongBits(other.latitudeOfStation))
			return false;
		if (Double.doubleToLongBits(longitudeOfStation) != Double
				  .doubleToLongBits(other.longitudeOfStation))
			return false;
		if (month == null) {
			if (other.month != null)
				return false;
		} else if (!month.equals(other.month))
			return false;
					
		if (year == null) {
			if (other.year != null)
				return false;
		} else if (!year.equals(other.year))
			return false;
			
		return true;
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		
		builder.append("InfoData [Id= ").append(id)
		       .append(", Arquivo de Entrada=").append(inputData)
		       .append(", Nome da Estação= ").append(station)
		       .append(", Data de Validação= ").append(dateOfValidation)
		       .append(", Latitude da Estação= ").append(latitudeOfStation)
		       .append(", Longitude da Estação= ").append(longitudeOfStation)
		       .append(", Observação= ").append(observation)
		       .append("]");
		return builder.toString();
	}
}