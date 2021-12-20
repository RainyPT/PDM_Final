 const express = require("express");
const mysql = require("mysql");
const bcrypt = require("bcrypt");
const app = express();
const port = 27015;
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
app.post("/addPresence",(req,res)=>{
  let ID=req.body.ID;
  let WifiName=req.body.WifiName;
  let IDAu=req.body.IDAu;
  let coords=[req.body.X,req.body.Y];
  dbase.query("SELECT * FROM presencas WHERE IDA="+ID+" AND CURDATE()=DATE(Data) AND IDAu="+IDAu,(err,resDB1)=>{
    if(err) throw err;
    if(resDB1.length===0){
      dbase.query("SELECT IDAu FROM inscricao WHERE IDA="+ID+" AND IDAu="+IDAu,(err,resDB2)=>{
        if(err) throw err;
        console.log(resDB2)
        if(resDB2.length===0){
          dbase.query("INSERT INTO inscricao(IDAu,IDA) VALUES("+IDAu+","+ID+")",(err,resDB3)=>{
            if(err) throw err;
          });
        }
      })
      dbase.query("SELECT Hora FROM aulas WHERE IDAu="+IDAu+" AND HOUR(NOW()) BETWEEN Hora AND (Hora+1)",(err,resDB3)=>{
        if(err)throw err;
        if(resDB3.length>0){
          dbase.query("INSERT INTO presencas(IDAu,IDA,NomeRede,Data,X,Y) VALUES("+IDAu+","+ID+","+WifiName+",NOW(),"+coords[0]+","+coords[1]+")",(err,resDB1)=>{
            if(err) throw err;
            res.send({status:1})
          });
        }
        else{
          res.send({status:0});
        }
      })
    }
    else{
      res.send({status:0});
    }
  })
});
//Now we talking
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

app.post("/registerCadeiras", (req, res) => {
  
  let cadeira = req.body.nomeCadeira;

  isCadeiraPresent = "SELECT Nome FROM cadeiras WHERE Nome='" + cadeira + "'";
 
  dbase.query(isCadeiraPresent, (err, resultDB1) => {
    if (err) throw err;
    if (resultDB1.length > 0) {
      res.send({ status: 0 });
    } else {
      let addUserQuery;
            addUserQuery = "INSERT INTO cadeiras (Nome) VALUES('" + cadeira +"')";
          dbase.query(addUserQuery, (err, resultDB2) => {
            if (err) throw err;
            res.send({ status: 1 });
          });
    }
  });
});

app.post("/getPresencasOfAluno",(req,res)=>{
  let IDA=req.body.IDA;
  let IDAu=req.body.IDAu;
  queryGetAllMissedClasses="SELECT CAST(Data AS DATE) as Data FROM presencas WHERE IDAu="+IDAu+" AND IDA!="+IDA+" GROUP BY CAST(Data AS DATE)"
  dbase.query(queryGetAllMissedClasses, (err, resultDB2) =>{
    if(err)throw err;
    res.send({status:1,faltas:resultDB2})
  })
})
app.post("/getEnrolledClasses",(req,res)=>{
queryAllClasses="SELECT aulas.Tipo,cadeiras.Nome,IDAuTable.IDAu FROM(SELECT IDAu FROM inscricao WHERE IDA="+req.body.IDA+") as IDAuTable,aulas,cadeiras WHERE IDAuTable.IDAu=aulas.IDAu AND aulas.IDC=cadeiras.IDC";
  dbase.query(queryAllClasses, (err, resultDB1) =>{
    if(err) throw err;
    res.send({status:1,cadeiras:resultDB1})
  })
})
app.post("/getPresencas",(req,res)=>{
  queryAllPresencas="SELECT IDA FROM presencas WHERE IDAu="+req.body.IDAu+" AND CAST(Data AS DATE)=CURDATE()"
  dbase.query(queryAllPresencas, (err, resultDB) =>{
    if(err) throw err;

    queryAllInscritos="SELECT COUNT(IDA) as TOTAL FROM inscricao WHERE IDAu="+req.body.IDAu
    dbase.query(queryAllInscritos, (err, resultDB2) =>{
      if(err) throw err;
      console.log(parseFloat(resultDB2[0].TOTAL));
      res.send({status:1,presencas:resultDB,assMedia:(resultDB.length/parseFloat(resultDB2[0].TOTAL)*100)})
    })
  })
})
app.get("/getCadeiras", (req,res) => {

  queryAllCadeiras = "SELECT * FROM cadeiras";

  dbase.query(queryAllCadeiras, (err, resultDB) =>{
    if(err) throw err;
    res.send({ status: 1, cadeiras:resultDB});
  }
  
  ) 
})
app.post("/getAulas", (req,res) => {
  let IDCadeira=req.body.cadeiraID;
  queryAllAulas = "SELECT IDAu,Tipo FROM aulas WHERE IDC="+IDCadeira;

  dbase.query(queryAllAulas, (err, resultDB) =>{
    if(err) throw err;
    res.send({ status: 1, aulas:resultDB});
  }) 
})
app.post("/addAula", (req,res) => {
  let IDP=req.body.IDP;
  let Tipo=req.body.Tipo;
  let DiaDaSemana=req.body.DiaDaSemana;
  let Hora=req.body.Hora;
  let IDC=req.body.IDC;
  let checkIfClassExists="SELECT Tipo FROM aulas WHERE IDC="+IDC+" AND Tipo='"+Tipo+"'";
  dbase.query(checkIfClassExists, (err, resultDB) =>{
    if(err) throw err;
    if(resultDB.length==0){
      let addClassQuery="INSERT INTO aulas (Hora,DiaDaSemana,Tipo,IDP,IDC) VALUES(" + Hora +",'"+DiaDaSemana+"','"+Tipo+"',"+IDP+","+IDC+")";
      dbase.query(addClassQuery, (err, resultDB) =>{
        if(err) throw err;
        res.send({ status: 1, aulas:resultDB});
      })
    }
    else{
      res.send({ status: 0});
    }
    
  }) 
})

app.listen(port, () => {
  console.log("http://localhost:" + port);
});
