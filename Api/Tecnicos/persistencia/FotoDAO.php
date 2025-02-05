<?php 
class FotoDAO{
    private $foto;
    private $id_obs_ticket;
    private $fecha;
    private $ubicacion;
    

    public function __construct($foto="", $id_obs_ticket=0, $fecha="", $ubicacion=null){
        $this->foto = $foto;
        $this->id_obs_ticket = $id_obs_ticket;
        $this->fecha = $fecha;
        $this->ubicacion = $ubicacion;
    }

    public function guardar(){
        return "INSERT INTO Foto_soporte (fecha, foto, ubicacion, id_obs_ticket)
        VALUES ('$this->fecha', 'https://app.inttelgo.com/Tecnicos/image/$this->foto', '$this->ubicacion', $this->id_obs_ticket)
        ";
    }
 
    public function guardarI($idIn){
        return "INSERT INTO Foto_insta (fecha, link, ubicacion, id_instalacion)
        VALUES ('$this->fecha', 'https://app.inttelgo.com/Tecnicos/image/$this->foto', '$this->ubicacion', $idIn)
        ";
    }

    public function consultarPorInstalacion(){
        return "SELECT link, fecha, ubicacion
            FROM Foto_insta
            WHERE id_instalacion = $this->id_obs_ticket
        ";
    }
}
?>