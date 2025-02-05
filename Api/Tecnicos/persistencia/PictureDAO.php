<?php 
class PictureDAO{
    private $fecha;
    private $ubicacion;
    private $foto;
    private $id_obs_ticket;
    
    public function __construct($id_obs_ticket=null, $fecha=null, $ubicacion=null, $foto=null){
        $this->fecha = $fecha;
        $this->ubicacion = $ubicacion;
        $this->foto = $foto;
        $this->id_obs_ticket = $id_obs_ticket;
    }

    public function consultarpictures(){
        return "SELECT fecha, ubicacion, foto
            FROM foto_soporte
            WHERE id_obs_ticket = ".$this->id_obs_ticket."
        ";
    }

}
?>