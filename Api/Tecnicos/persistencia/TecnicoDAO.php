<?php 
class TecnicoDAO{
    private $id_usuario;
    private $nombre_1;
    private $apellido_1;
    private  $usuario_login; //nombre del usuario
    private  $pass_login; //contraseña del usuario
    private $id_estado_usuario = 1; //activo =1, inacctivo =2
    private $id_perfil = 6; //rol

    private $color;

    public function __construct($id_usuario= 0, $nombre_1="", $apellido_1="", $usuario_login = "", $pass_login ="", $color=""){
        $this-> id_usuario = $id_usuario;
        $this->nombre_1 = $nombre_1;
        $this->apellido_1 = $apellido_1;
        $this->usuario_login = $usuario_login;
        $this->pass_login = $pass_login;
        $this->color = $color;
    }

    public function consultarCredenciales(){
        return "SELECT id_usuario, color
                FROM usuario_has_profile
                WHERE usuario_login = '". $this-> usuario_login."' 
                    AND pass_login = '". $this-> pass_login."' 
                    AND id_estado_usuario= ". $this-> id_estado_usuario." 
                    AND (id_perfil = 6 OR id_perfil = 1 OR id_perfil= 3)
                ";
    }


    public function consultarPorId(){
        return "SELECT nombre_1, apellido_1
                FROM usuario
                WHERE id_usuario = ". $this->id_usuario."
                ";
    }
}

?>