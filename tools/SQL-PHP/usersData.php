<?php
require_once "linkSQL.php";


$dateTime = $_POST["dateTime"];
$user = $_POST["user"];
$deviceName = $_POST["deviceName"];
$appVersion = $_POST["appVersion"];
$systemVersion = $_POST["systemVersion"];
$studentID = $_POST["studentID"];

$sql = "INSERT INTO `UserData` (`dateTime`, `user`, `deviceName`, `appVersion`, `systemVersion`, `studentID`) VALUES ('$dateTime', '$user', '$deviceName', '$appVersion', '$systemVersion', '$studentID');";
 
if ($conn->query($sql) === TRUE) {
    echo "插入成功";
} else {
    echo "Error: " . $sql . "<br>" . $conn->error;
}
 
$conn->close();
?>