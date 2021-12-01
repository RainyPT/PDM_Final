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
    database: "tisisme"
});

dbase.connect(function(err) {
    if (err) throw err;
    console.log("Database Connected!");
});


//Now we talking
app.post("/login",(req,res)=>{
    let password=req.body.password;
    let isUsernamePresent;
    let numero=req.body.numero;
    let isProf=req.body.isProf;
    if(isProf==undefined){
        
        isUsernamePresent="SELECT Password FROM alunos WHERE IDA="+numero;
    }
    else{
        isUsernamePresent="SELECT IDP,Password FROM professores WHERE Username='"+numero+"';";
    }
    dbase.query(isUsernamePresent,(err,resultDB1)=>{
        if(err) throw err;
        if(resultDB1.length==0){
            res.send({"status":0});
        }
        else{
            bcrypt.compare(password, resultDB1[0].Password, (err, result) => {
                if(err)throw err;
                if(result){
                    if(isProf==undefined){
                        res.send({"status":1,"ID":numero});
                    }
                    else{
                        res.send({"status":1,"ID":resultDB1[0].IDP});
                    }
                }
                else{
                    res.send({"status":0});
                }
            });
        }
    });
})
app.post("/register",(req,res)=>{
    let numero=req.body.numero;
    let email=req.body.email;
    let plainTextPassword=req.body.password;
    console.log(req.body.isProf);
    let isProf=req.body.isProf;
    if(isProf==undefined){
        
        isUsernamePresent="SELECT IDA FROM alunos WHERE IDA="+numero;
    }
    else{
        isUsernamePresent="SELECT Username FROM professores WHERE Username='"+numero+"';";
    }
    dbase.query(isUsernamePresent,(err,resultDB1)=>{
        if(err)throw err;
        if(resultDB1.length>0){
            res.send({"status":0});
        }
        else{
            bcrypt.genSalt(10, function(err, salt) {
                if(err)throw err;
                bcrypt.hash(plainTextPassword, salt, function(err, hash) {
                    if(err)throw err;
                    let addUserQuery;
                    if(isProf==undefined){
                        addUserQuery="INSERT INTO alunos (IDA,Email,Password) VALUES('"+numero+"','"+email+"','"+hash+"')";
                    }
                    else{
                        addUserQuery="INSERT INTO professores (Username,Email,Password) VALUES('"+numero+"','"+email+"','"+hash+"')";
                    }
                    dbase.query(addUserQuery,
                        (err,resultDB2)=>{
                        if(err)throw err;
                        res.send({"status":1});
                    })
                });
            });
        }
    })
})

app.listen(port, () => {
    console.log("http://localhost:" + port);
});