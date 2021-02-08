var mysql = require('mysql');

var connection = mysql.createConnection({
  host     : '127.0.0.1',
  user     : 'root',
  password : '',
  database : 'gamedatabase'
});

connection.connect(function(err) {
  if (err) {
    console.error('Error connecting: ' + err.stack);
    return;
  }
  console.log('Connected as id ' + connection.threadId);

  connection.query('INSERT INTO users (email, username, password) VALUES (?, ?, ?)', ['abcd', 'abcd', 'abcd'], function (error, results, fields) {
    if (error) throw error;
    console.log(error, results, fields);
  });

  connection.end();
});
