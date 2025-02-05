<?php
class Tecnico extends Usuario{
    private  $usuario_login; //nombre del usuario
    private  $pass_login; //contraseña del usuario
    private $id_estado_usuario = 1; //activo =1, inacctivo =2
    private $id_perfil = 6; //rol
    private $color;

    public function setUsuario_login($usuario_login){
        $this->usuario_login = $usuario_login;
    }
    
    public function getUsuario_login(){
        return $this->usuario_login;
    }
    
    public function setPass_login($pass_login){
        $this->pass_login = $pass_login;
    }
    
    public function getPass_login(){
        return $this->pass_login;
    }
    
    public function setId_estado_usuario($id_estado_usuario){
        $this->id_estado_usuario = $id_estado_usuario;
    }
    
    public function setId_perfil($id_perfil){
        $this->id_perfil = $id_perfil;
    }
    
    public function getColor(){
        return $this->color;
    }
    
    public function setColor($color){
        $this->color = $color;
    }

    public function __construct($usuario_login = "", $pass_login ="", $id_usuario= 0, $nombre_1="", $apellido_1="", $color=""){
        parent::__construct($id_usuario, $nombre_1, $apellido_1);
        $this->usuario_login = $usuario_login;
        $this->pass_login = $pass_login;
        $this->color = $color;
    }

    public function consultar(){
        $conexion = new Conexion();
        $conexion->abrirConexion();
        $tecnicoDAO = new TecnicoDAO(null, null, null ,$this->usuario_login, 
        $this->pass_login, null);
        $conexion->ejecutarConsulta($tecnicoDAO->consultarCredenciales());
        if($conexion->numeroFilas() > 0){
            $registro = $conexion->siguienteRegistro();
            $this->id_usuario = $registro[0];
            $this->color = $registro[1];
            $conexion->cerrarConexion();
            return true;
        }
        $conexion->cerrarConexion();
        return false;
    }

    public function consultarPorId(){
        $conexion = new Conexion();
        $conexion->abrirConexion();
        $tecnicoDAO = new TecnicoDAO($this->id_usuario);
        $conexion->ejecutarConsulta($tecnicoDAO->consultarPorId());
        if($conexion->numeroFilas() > 0){
            $registro = $conexion->siguienteRegistro();
            $this->nombre_1 = $registro[0];
            $this->apellido_1 = $registro[1];
            $conexion->cerrarConexion();
            return true;
        }
        $conexion->cerrarConexion();
        return false;
    }
}

?>