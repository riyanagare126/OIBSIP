/**
 * Model representing a single train reservation record stored in SQLite.
 */
public class ReservationRecord {
    private final String pnr;
    private final String passengerName;
    private final int trainNumber;
    private final String trainName;
    private final String classType;
    private final String journeyDate;
    private final String sourceStation;
    private final String destinationStation;
    private final String bookedBy;
    private final String bookedAt;

    public ReservationRecord(
            String pnr,
            String passengerName,
            int trainNumber,
            String trainName,
            String classType,
            String journeyDate,
            String sourceStation,
            String destinationStation,
            String bookedBy,
            String bookedAt) {
        this.pnr = pnr;
        this.passengerName = passengerName;
        this.trainNumber = trainNumber;
        this.trainName = trainName;
        this.classType = classType;
        this.journeyDate = journeyDate;
        this.sourceStation = sourceStation;
        this.destinationStation = destinationStation;
        this.bookedBy = bookedBy;
        this.bookedAt = bookedAt;
    }

    public String getPnr() {
        return pnr;
    }

    public String getPassengerName() {
        return passengerName;
    }

    public int getTrainNumber() {
        return trainNumber;
    }

    public String getTrainName() {
        return trainName;
    }

    public String getClassType() {
        return classType;
    }

    public String getJourneyDate() {
        return journeyDate;
    }

    public String getSourceStation() {
        return sourceStation;
    }

    public String getDestinationStation() {
        return destinationStation;
    }

    public String getBookedBy() {
        return bookedBy;
    }

    public String getBookedAt() {
        return bookedAt;
    }

    public String toFormattedSummary() {
        return String.format(
                "PNR Number         : %s%n" +
                "Passenger Name     : %s%n" +
                "Train Number       : %d%n" +
                "Train Name         : %s%n" +
                "Class Type         : %s%n" +
                "Date of Journey    : %s%n" +
                "Source Station     : %s%n" +
                "Destination Station: %s%n" +
                "Booked By          : %s%n" +
                "Booking Timestamp  : %s",
                pnr, passengerName, trainNumber, trainName,
                classType, journeyDate, sourceStation, destinationStation,
                bookedBy, bookedAt
        );
    }
}

