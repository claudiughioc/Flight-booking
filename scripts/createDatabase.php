<?php
$db_host = "localhost";
$username = "root";
$password = "claudiu";
$db_name = "airservice";

// Connect tot the mysql server
$con = mysql_connect($db_host, $username, $password);
if (!$con)
    die("Could not connect to db: " . mysql_error());
echo "Connected to the mysql server on " . $db_host . "\n";

// Create the database
if (!mysql_query("CREATE DATABASE " . $db_name))
    die("Error creating database: " . mysql_error());
echo "Created database " . $db_name . "\n";

// Create the tables
mysql_select_db($db_name, $con);
$sql = "create table Flight (
        id int(11) PRIMARY KEY AUTO_INCREMENT NOT NULL,
            flight_id_official varchar(255),
    source varchar(255),
    destination varchar(255),
    hour int(11),
    day int(11),
    duration int(11),
    state int(11),
    total_seats int(11),
    booked_seats int(11));";
if (!mysql_query($sql,$con))
    die("Could not create table Flight: " . mysql_error());
echo "Created table Flight\n";


$sql = "create table Reservation (
        id int(11) PRIMARY KEY AUTO_INCREMENT NOT NULL,
            date datetime);";
if (!mysql_query($sql,$con))
    die("Could not create table Reservation: " . mysql_error());
echo "Created table Reservation\n";


$sql = "create table FlightReservation (
        id int(11) PRIMARY KEY AUTO_INCREMENT NOT NULL,
            flight_id int(11),
    reservation_id int(11),
    KEY (flight_id),
    FOREIGN KEY (flight_id) REFERENCES Flight(id),
    FOREIGN KEY (reservation_id) REFERENCES Reservation(id));";
if (!mysql_query($sql,$con))
    die("Could not create table FlightReservation: " . mysql_error());
echo "Created table FlightReservation\n";


$sql = "create table Ticket (
        id int(11) PRIMARY KEY AUTO_INCREMENT NOT NULL,
            reservation_id int(11),
    creditCardInfo varchar(255),
    FOREIGN KEY (reservation_id) REFERENCES Reservation(id));";
if (!mysql_query($sql,$con))
    die("Could not create table Ticket: " . mysql_error());
echo "Created table Ticket\n";

// Close the connection to the DB
mysql_close($con);
echo "Connection closed. Database created successfuly\n";
?>
