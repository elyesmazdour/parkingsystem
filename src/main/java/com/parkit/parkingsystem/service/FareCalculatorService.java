package com.parkit.parkingsystem.service;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.model.Ticket;

public class FareCalculatorService {

	// public DataBaseConfig dataBaseConfig = new DataBaseConfig();
	private final TicketDAO ticketDAO = new TicketDAO();

	public void calculateFare(Ticket ticket) {
		if (ticket.getOutTime() == null || ticket.getOutTime().before(ticket.getInTime())) {
			throw new IllegalArgumentException("Out time provided is incorrect:" + ticket.getOutTime().toString());
		}

		final int inHour = (int) ticket.getInTime().getTime();
		final int outHour = (int) ticket.getOutTime().getTime();

		// TODO: Some tests are failing here. Need to check if this logic is correct
		// NOW IS CORRECT
		final int duration = (outHour - inHour) / 1000 / 60;

		// ticket is free when duration less 30 minutes
		if (duration <= 30) {
			ticket.setPrice(0);

		}

		else {
			switch (ticket.getParkingSpot().getParkingType()) {
			case CAR: {
				ticket.setPrice(duration * Fare.CAR_RATE_PER_HOUR / 60);
				break;
			}
			case BIKE: {
				ticket.setPrice(duration * Fare.BIKE_RATE_PER_HOUR / 60);
				break;
			}
			default:
				throw new IllegalArgumentException("Unkown Parking Type");
			}
		}

// Si le véhicule a déja été dans le parking au moins une fois pour
// appliquer la remise 5%

		final Ticket existingTicket = ticketDAO.getTicket(ticket.getVehicleRegNumber());

		System.out.println(existingTicket);
		if (existingTicket != null) {

			ticket.setPrice(ticket.getPrice() - ticket.getPrice() * 0.05);
		}
	}
}