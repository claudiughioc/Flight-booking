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

$sql = "
    insert into Flight(flight_id_official, source, destination, day, hour, duration, state, total_seats, booked_seats)
    value
    ('1234', 'Bucuresti', 'Budapesta', 10, 10, 1, 0, 100, 10);";
if (!mysql_query($sql,$con))
    die("Could not insert test data: " . mysql_error());
echo "Inserted test data\n";

$sql = "
insert into Flight(flight_id_official, source, destination, day, hour, duration, state, total_seats, booked_seats)
    value
    ('1235', 'Bucuresti', 'Viena', 10, 11, 1, 0, 100, 10);
";
if (!mysql_query($sql,$con))
    die("Could not insert test data: " . mysql_error());
echo "Inserted test data\n";

$sql = "
insert into Flight(flight_id_official, source, destination, day, hour, duration, state, total_seats, booked_seats)
    value
    ('1236', 'Bucuresti', 'Roma', 10, 11, 1, 0, 100, 10);
";
if (!mysql_query($sql,$con))
    die("Could not insert test data: " . mysql_error());
echo "Inserted test data\n";

$sql = "
insert into Flight(flight_id_official, source, destination, day, hour, duration, state, total_seats, booked_seats)
    value
    ('1237', 'Budapesta', 'Viena', 10, 14, 1, 0, 100, 10);
";
if (!mysql_query($sql,$con))
    die("Could not insert test data: " . mysql_error());
echo "Inserted test data\n";

$sql = "
insert into Flight(flight_id_official, source, destination, day, hour, duration, state, total_seats, booked_seats)
    value
    ('1238', 'Roma', 'Lisabona', 10, 20, 2, 0, 100, 10);
";
if (!mysql_query($sql,$con))
    die("Could not insert test data: " . mysql_error());
echo "Inserted test data\n";

$sql = "
insert into Flight(flight_id_official, source, destination, day, hour, duration, state, total_seats, booked_seats)
    value
    ('1239', 'Lisabona', 'Londra', 11, 2, 2, 0, 100, 10);
";
if (!mysql_query($sql,$con))
    die("Could not insert test data: " . mysql_error());
echo "Inserted test data\n";

$sql = "
insert into Flight(flight_id_official, source, destination, day, hour, duration, state, total_seats, booked_seats)
    value
    ('1240', 'Viena', 'Madrid', 10, 13, 1, 0, 100, 10);
";
if (!mysql_query($sql,$con))
    die("Could not insert test data: " . mysql_error());
echo "Inserted test data\n";
$sql = "
insert into Flight(flight_id_official, source, destination, day, hour, duration, state, total_seats, booked_seats)
    value
    ('1242', 'Madrid', 'Londra', 11, 06, 3, 0, 100, 10);
";
if (!mysql_query($sql,$con))
    die("Could not insert test data: " . mysql_error());
echo "Inserted test data\n";
$sql = "
insert into Flight(flight_id_official, source, destination, day, hour, duration, state, total_seats, booked_seats)
    value
    ('1241', 'Madrid', 'Lisabona', 10, 15, 1, 0, 100, 10);
";
if (!mysql_query($sql,$con))
    die("Could not insert test data: " . mysql_error());
echo "Inserted test data\n";

$sql = "
insert into Flight(flight_id_official, source, destination, day, hour, duration, state, total_seats, booked_seats)
    value
    ('1243', 'Budapesta', 'Berlin', 10, 17, 1, 0, 100, 10);
";
if (!mysql_query($sql,$con))
    die("Could not insert test data: " . mysql_error());
echo "Inserted test data\n";

$sql = "
insert into Flight(flight_id_official, source, destination, day, hour, duration, state, total_seats, booked_seats)
    value
    ('1244', 'Viena', 'Berlin', 11, 06, 1, 0, 100, 10);
";
if (!mysql_query($sql,$con))
    die("Could not insert test data: " . mysql_error());
echo "Inserted test data\n";

$sql = "
insert into Flight(flight_id_official, source, destination, day, hour, duration, state, total_seats, booked_seats)
    value
    ('1245', 'Berlin', 'Londra', 11, 04, 2, 0, 100, 10);
";
if (!mysql_query($sql,$con))
    die("Could not insert test data: " . mysql_error());
echo "Inserted test data\n";



// Close the connection to the DB
mysql_close($con);
echo "Connection closed. Database created successfuly\n";
?>
