package com.yalnizfahrettin.azim.notif

import android.content.Context
import androidx.work.*
import com.yalnizfahrettin.azim.data.Depo
import com.yalnizfahrettin.azim.data.Sozler
import kotlinx.coroutines.flow.first
import java.time.Duration
import java.time.LocalDateTime
import java.util.concurrent.TimeUnit

object Planlayici {
    private const val GUNLUK_IS = "azim_gunluk_plan"
    private const val ERTESI_GUN = "ascend_ertesi_gun"
    private const val DILIM_ONEK = "azim_dilim_"

    fun yenidenKur(ctx: Context) {
        WorkManager.getInstance(ctx).enqueueUniqueWork(GUNLUK_IS, ExistingWorkPolicy.REPLACE, OneTimeWorkRequestBuilder<PlanWorker>().build())
    }

    internal suspend fun dilimleriKur(ctx: Context) {
        val wm = WorkManager.getInstance(ctx)
        val depo = Depo(ctx)
        // Remove every old slot, including surplus slots when frequency is reduced.
        repeat(7) { wm.cancelUniqueWork(DILIM_ONEK + it) }
        wm.cancelUniqueWork(ERTESI_GUN)
        depo.planliSaatleriYaz(emptyList())
        if (!depo.onboardingBitti.first() || !depo.hatirlaticiAcik.first() || !Bildirimler.izinVarMi(ctx)) return
        val simdi = LocalDateTime.now()
        val saatler = BildirimZamanlari.hesapla(simdi, depo.gunlukAdet.first(), depo.baslangicSaati.first(), depo.bitisSaati.first())
        saatler.forEachIndexed { i, an ->
            wm.enqueueUniqueWork(DILIM_ONEK + i, ExistingWorkPolicy.REPLACE,
                OneTimeWorkRequestBuilder<SozWorker>()
                    .setInitialDelay(Duration.between(simdi, an).seconds.coerceAtLeast(1), TimeUnit.SECONDS).build())
        }
        depo.planliSaatleriYaz(saatler)
        val yarin = simdi.toLocalDate().plusDays(1).atTime(0, 5)
        wm.enqueueUniqueWork(ERTESI_GUN, ExistingWorkPolicy.REPLACE,
            OneTimeWorkRequestBuilder<YenileWorker>()
                .setInitialDelay(Duration.between(simdi, yarin).seconds, TimeUnit.SECONDS).build())
    }
}

class PlanWorker(ctx: Context, params: WorkerParameters) : CoroutineWorker(ctx, params) {
    override suspend fun doWork(): Result {
        Planlayici.dilimleriKur(applicationContext)
        return Result.success()
    }
}

/** A separate worker enqueues planning so the active plan never replaces itself. */
class YenileWorker(ctx: Context, params: WorkerParameters) : CoroutineWorker(ctx, params) {
    override suspend fun doWork(): Result {
        Planlayici.yenidenKur(applicationContext)
        return Result.success()
    }
}

class SozWorker(ctx: Context, params: WorkerParameters) : CoroutineWorker(ctx, params) {
    override suspend fun doWork(): Result {
        val depo = Depo(applicationContext)
        if (!depo.onboardingBitti.first() || !depo.hatirlaticiAcik.first() || !Bildirimler.izinVarMi(applicationContext)) return Result.success()
        val saat = java.time.LocalTime.now().hour
        if (saat < depo.baslangicSaati.first() || saat >= depo.bitisSaati.first()) return Result.success()
        val havuz = Sozler.bildirimHavuzu(depo.secili.first(), depo.dil.first())
        val gecmis = depo.gecmis.first()
        val soz = havuz.filterNot { it.kimlik in gecmis }.ifEmpty { havuz }.randomOrNull() ?: return Result.success()
        if (Bildirimler.goster(applicationContext, soz, depo.dil.first())) {
            depo.bildirimGecmisineEkle(soz.kimlik)
            depo.bugunGeldi(soz.kimlik)
        }
        return Result.success()
    }
}
