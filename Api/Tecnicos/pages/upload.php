<?php
if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    if (isset($_FILES['image']) && $_FILES['image']['error'] === UPLOAD_ERR_OK) {
        $fechaActual = date("Y-m-d H:i:s");
        $uploadDir = "image/" ;

        // Crear la carpeta si no existe
        if (!is_dir($uploadDir)) {
            mkdir($uploadDir, 0777, true);
        }
        $uploadFile = $uploadDir . basename($_FILES['image']['name']);
        if ($_POST["tipo"] == "Soporte" || $_POST["tipo"] == "Finalizar") {
            $foto_soporte = new Foto($_FILES['image']['name'], $_POST["idObs"], $fechaActual, $_POST["ubication"]);
            $foto_soporte->guardar();
        } else {
            $foto_instalacion = new Foto($_FILES['image']['name'], null, $fechaActual, $_POST["ubication"]);
            $foto_instalacion->guardarI($_POST["idIn"]);
        }
 
        // Procesar la imagen con GD
        $imageType = exif_imagetype($_FILES['image']['tmp_name']); 
        $image = null;

        switch ($imageType) {
            case IMAGETYPE_JPEG:
                $image = imagecreatefromjpeg($_FILES['image']['tmp_name']);
                break;
            case IMAGETYPE_PNG:
                $image = imagecreatefrompng($_FILES['image']['tmp_name']);
                break;
            case IMAGETYPE_GIF:
                $image = imagecreatefromgif($_FILES['image']['tmp_name']);
                break;
            default:
                echo json_encode(["success" => false, "message" => "Unsupported image type"]);
                exit;
        }

        if ($image) {
            $fontdir = __DIR__."/../text/SerifR.ttf";
            $textColor = imagecolorallocate($image, 255, 255, 255); // Color blanco
            // Separar componentes
            $hora = date("H:i:s");
            $fontSize = 30;

            $width = imagesx($image);
            $height = imagesy($image);
            $x = 30.0;
            $y = $height * 0.75;

            imagettftext($image,$fontSize, 0, $x, $y, $textColor, $fontdir, $hora);
            
            $fecha = date("Y/m/d");
            $fontSize = 15;
            $y += 50.0;   
            imagettftext($image,$fontSize, 0, $x, $y, $textColor, $fontdir, $fecha);
            
            $ubicacion = $_POST["ubication"];
            $fontSize = 15;
            $y += 20;
            imagettftext($image,$fontSize, 0, $x, $y, $textColor, $fontdir,  html_entity_decode($ubicacion)); 

            $tecnico = new Tecnico(null,null,$_POST["idTec"]);

            $tecnico ->consultarPorId();

            $nombreTecnico = $tecnico->getNombre_1(). " ". $tecnico->getApellido_1();
            $y += 40;
            imagettftext($image,$fontSize, 0, $x, $y, $textColor, $fontdir,  html_entity_decode($nombreTecnico)); 

            switch ($imageType) {
                case IMAGETYPE_JPEG:
                    imagejpeg($image, $uploadFile); 
                    break;
                case IMAGETYPE_PNG:
                    imagepng($image, $uploadFile);
                    break;
                case IMAGETYPE_GIF:
                    imagegif($image, $uploadFile);
                    break;
            }
        }

        imagedestroy($image); 

        echo json_encode(["success" => true, "message" => "Image uploaded and processed successfully"]);
    } else {
        echo json_encode(["success" => false, "message" => "No file uploaded or upload error"]);
    }
}else {
    echo json_encode(["success" => false, "message" => "Invalid request method"]);
}
