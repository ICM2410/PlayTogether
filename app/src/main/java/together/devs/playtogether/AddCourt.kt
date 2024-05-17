package together.devs.playtogether

import android.content.pm.PackageManager
import android.Manifest
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
class AddCourt : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_court)
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {

            }
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), CAMARA)
        }
    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMARA) {
            for (i in permissions.indices) {
                val permission = permissions[i]
                val grantResult = grantResults[i]
                if (permission == Manifest.permission.CAMERA) {
                    if (grantResult == PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(this, "PERMISO CAMARA DADO", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, "PERMISO CAMARA DADO", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    companion object {
        private const val CONTACTOS = 0
        const val CAMARA = 1
    }
}