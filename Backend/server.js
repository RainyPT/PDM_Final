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
  let IDP2A=req.body.IDP2A;
  let coords=[req.body.X,req.body.Y];
  dbase.query("SELECT IDAu FROM p2a WHERE IDP2A="+IDP2A,(err,resDB1)=>{
    if (err) throw err;
    dbase.query("SELECT IDC FROM aulas WHERE IDAu="+resDB1[0].IDAu,(err,resDB2)=>{
    dbase.query("SELECT * FROM inscricao WHERE IDC="+resDB2[0].IDC+" AND IDA="+ID,(err,resDB3)=>{
      if (err) throw err;
      if(resDB3.length==0){
        dbase.query("INSERT INTO inscricao(IDC,IDA) VALUES("+resDB2[0].IDC+","+ID+")",(err,resDBx)=>{
          if (err) throw err;
        })
      }
    })
  })
  })
  dbase.query("SELECT IDP2A FROM presencas WHERE IDA="+ID+" AND IDP2A="+IDP2A,(err,resDB1)=>{
    if (err) throw err;
    if(resDB1.length==0){
      dbase.query("Insert into presencas (X,Y,NomeRede,IDA,IDP2A) VALUES("+coords[0]+","+coords[1]+","+WifiName+","+ID+","+IDP2A+")", (err, resDB2) => {
        if (err) throw err;
        res.send({"status":1})
      });
    }
    else{
      res.send({"status":0})
    }
  });
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
  queryGetIDP2A="SELECT IDP2A FROM presencas WHERE IDA="+req.body.IDA;
  dbase.query(queryGetIDP2A, (err, resultDB) =>{
    let Nomes=[]
    let Datas=[]
    for(let i=0;i<resultDB.length;i++){
      let getIDCQuery="SELECT Nome,DATE(IDCTable.Data)as Data FROM cadeiras,(SELECT IDC,IDAuTable.Data FROM(SELECT IDAu,Data FROM p2a WHERE IDP2A="+resultDB[i].IDP2A+") as IDAuTable,aulas WHERE aulas.IDAu=IDAuTable.IDAu) as IDCTable WHERE cadeiras.IDC=IDCTable.IDC"
      dbase.query(getIDCQuery ,(err, resultDB2) =>{
        if(err)throw err;
        Nomes[i]=resultDB2[i].Nome;
        console.log(resultDB2[i])
        Datas[i]=resultDB2[i].Data;
        if(i+1==resultDB.length){
          res.send({CadeirasNomes:Nomes,AulasData:Datas})
        }

      })
    }
  })
})
app.post("/getPresencas",(req,res)=>{
  queryGetIDP2A="SELECT IDP2A FROM p2a WHERE IDAu="+req.body.IDAu+" AND Data=CURDATE()"
  dbase.query(queryGetIDP2A, (err, resultDB) =>{
    if(err) throw err;
    if(resultDB.length>0){
      queryAllPresencas="SELECT IDA FROM presencas WHERE IDP2A="+resultDB[0].IDP2A
      dbase.query(queryAllPresencas, (err, resultDB2) =>{
        if(err) throw err;
        res.send({status:1,presencas:resultDB2})
      })
    }
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
app.post("/getIDP2A", (req,res) => {
  let IDAula=req.body.IDAu;
  let addIDAuQuery="INSERT INTO p2a (IDAu,Data) VALUES("+IDAula+",CURDATE())";
  let getIDP2AQuery="SELECT IDP2A FROM p2a WHERE IDAu="+IDAula+" AND Data=CURDATE()"

  dbase.query(getIDP2AQuery, (err, resultDB) =>{
    if(err) throw err;
    if(resultDB.length>0){
      res.send({status:1,IDP2A:resultDB[0].IDP2A})
    }
    else{
      dbase.query(addIDAuQuery, (err, resultD2) =>{
        if(err) throw err;
        dbase.query(getIDP2AQuery, (err, resultDB) =>{
          res.send({status:1,IDP2A:resultDB[0].IDP2A})
        })
      })
    }
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
