const express = require("express");
const mysql = require("mysql");
const bcrypt = require("bcrypt");
const app = express();
const port = 12345;
app.use(
  express.urlencoded({
    extended: true,
  })
);
app.use(express.json());
app.use(express.static("public"));
const dbase = mysql.createConnection({
  host: "localhost",
  user: "root",
  password: null,
  database: "tisisme",
});

dbase.connect(function (err) {
  if (err) throw err;
  console.log("Database Connected!");
});
app.post("/updateUserInfo", (req, res) => {
  let idType = "IDA";
  let TableToUpdate = "alunos";
  if (req.body.Tipo === "Professor") {
    TableToUpdate = "professores";
    idType = "IDP";
  }
  if (req.body.PN != "") {
    let ChangeQuery =
      "UPDATE " +
      TableToUpdate +
      " SET PrimeiroNome='" +
      req.body.PN +
      "' WHERE " +
      idType +
      "=" +
      req.body.ID;
    dbase.query(ChangeQuery, (err, resultDB1) => {
      if (err) throw err;
    });
  }
  if (req.body.SN != "") {
    let ChangeQuery =
      "UPDATE " +
      TableToUpdate +
      " SET SegundoNome='" +
      req.body.SN +
      "' WHERE " +
      idType +
      "=" +
      req.body.ID;
    dbase.query(ChangeQuery, (err, resultDB1) => {
      if (err) throw err;
    });
  }
  if (req.body.Password != "") {
    bcrypt.hash(req.body.Password, salt, function (err, hash) {
      let ChangeQuery =
        "UPDATE " +
        TableToUpdate +
        " SET Password='" +
        req.body.Password +
        "' WHERE " +
        idType +
        "=" +
        req.body.ID;
      dbase.query(ChangeQuery, (err, resultDB1) => {
        if (err) throw err;
      });
    });
  }
  res.send({ status: 1 });
});
//Now we talking
app.post("/login", (req, res) => {
  let password = req.body.password;
  let isUsernamePresent;
  let numero = req.body.numero;
  let isProf = req.body.isProf;
  if (isProf) {
    isUsernamePresent =
      "SELECT IDP,PrimeiroNome,Password FROM professores WHERE Username='" +
      numero +
      "';";
  } else {
    isUsernamePresent =
      "SELECT PrimeiroNome,Password FROM alunos WHERE IDA=" + numero;
  }
  dbase.query(isUsernamePresent, (err, resultDB1) => {
    if (err) throw err;
    if (resultDB1.length == 0) {
      res.send({ status: 0 });
    } else {
      bcrypt.compare(password, resultDB1[0].Password, (err, result) => {
        if (err) throw err;
        if (result) {
          if (isProf) {
            res.send({
              status: 1,
              ID: resultDB1[0].IDP,
              PN: resultDB1[0].PrimeiroNome,
            });
          } else {
            res.send({ status: 1, ID: numero, PN: resultDB1[0].PrimeiroNome });
          }
        } else {
          res.send({ status: 0 });
        }
      });
    }
  });
});
app.post("/register", (req, res) => {
  let numero = req.body.numero;
  let email = req.body.email;
  let plainTextPassword = req.body.password;
  let isProf = req.body.isProf;
  if (isProf) {
    isUsernamePresent =
      "SELECT Username FROM professores WHERE Username='" + numero + "';";
  } else {
    isUsernamePresent = "SELECT IDA FROM alunos WHERE IDA=" + numero;
  }
  dbase.query(isUsernamePresent, (err, resultDB1) => {
    if (err) throw err;
    if (resultDB1.length > 0) {
      res.send({ status: 0 });
    } else {
      bcrypt.genSalt(10, function (err, salt) {
        if (err) throw err;
        bcrypt.hash(plainTextPassword, salt, function (err, hash) {
          if (err) throw err;
          let addUserQuery;
          if (isProf) {
            addUserQuery =
              "INSERT INTO professores (Username,Email,Password) VALUES('" +
              numero +
              "','" +
              email +
              "','" +
              hash +
              "')";
          } else {
            addUserQuery =
              "INSERT INTO alunos (IDA,Email,Password) VALUES('" +
              numero +
              "','" +
              email +
              "','" +
              hash +
              "')";
          }
          dbase.query(addUserQuery, (err, resultDB2) => {
            if (err) throw err;
            res.send({ status: 1 });
          });
        });
      });
    }
  });
});

app.listen(port, () => {
  console.log("http://localhost:" + port);
});
