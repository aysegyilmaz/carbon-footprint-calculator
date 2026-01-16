let myChart = null; // Grafiği her seferinde yeniden yaratmamak için

function downloadPDF() {
    console.log("PDF İndirme isteği gönderildi...");
    window.location.href = "/api/carbon/report/download";
}
const statsMap = { // Aktivite isimlerini HTML element ID'lerine eştirdim
    "Video İzleme": "videoStat",
    "E-posta": "emailStat",
    "Online Toplantı": "meetingStat",
    "Sosyal Medya": "socialStat"
};

document.addEventListener("DOMContentLoaded", () => {
    loadStats();
});

function calculate() {
    const activity = document.getElementById("activityName").value;
    const duration = document.getElementById("durationMinutes").value;

    if (!duration || duration <= 0) {
        alert("Lütfen geçerli bir süre giriniz");
        return;
    }

-
    fetch("/api/carbon/save", {     // Veriyi backend'e gönder
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
            activityName: activity,
            durationMinutes: parseInt(duration)
        })
    })
        .then(res => res.json())     // JSON yanıtını al
        .then(() => {
            loadStats();                //Yeni veri eklendi,Dashboard tekrar yüklendi, Input temizlendi.
            document.getElementById("durationMinutes").value = "";
        })
        .catch(err => console.error("Hata:", err));
}

function loadStats() {

    fetch("/api/carbon/summary")  // Backend'den özet veriyi al
        .then(res => res.json())
        .then(summaryData => {
            console.log("Backendden Gelen Özet:", summaryData);

            // Backend'den gelen Map içindeki TOTAL anahtarını alıyoruz
            if (summaryData["TOTAL"] !== undefined) {
                const totalAmount = summaryData["TOTAL"];
                document.getElementById("totalStat").innerHTML =
                    `${totalAmount.toFixed(2)} <small>kg</small>`;

                // Karbon limitini kontrol et (Limit: 10 kg)
                checkCarbonLimit(totalAmount);
            }

       -
            Object.keys(statsMap).forEach(activity => {  // Her aktivite için ilgili HTML elementini güncelle
                const id = statsMap[activity];
                const value = summaryData[activity] || 0;      // Eğer veri yoksa 0 kullan
                document.getElementById(id).innerHTML =
                    `${value.toFixed(2)} <small>kg</small>`;  //Kartları doldur 2 ondalık göster
            });
        })
        .catch(err => console.error("Yükleme Hatası:", err));

}


function showDaily() {
    // Menü aktif durumunu güncelle
    document.querySelectorAll('nav ul li').forEach(li => li.classList.remove('active'));
    if (event && event.target) {
        event.target.closest('li').classList.add('active');
    }
    
    // Tüm bölümleri gizle
    document.getElementById("infoCards").style.display = "none";
    document.getElementById("chartSection").style.display = "none";
    document.querySelector(".stats-grid").style.display = "none";
    document.querySelector(".top-bar h2").textContent = "Günlük Aktivite";
    
    // Günlük bölümü göster
    document.getElementById("dailySection").style.display = "block";
    
    // Günlük verileri yükle
    loadDailyStats();
}

function showStats() {
    // Menü aktif durumunu güncelle
    document.querySelectorAll('nav ul li').forEach(li => li.classList.remove('active'));
    if (event && event.target) {
        event.target.closest('li').classList.add('active');
    }
    
    document.getElementById("infoCards").style.display = "none";
    document.getElementById("dailySection").style.display = "none";
    document.querySelector(".stats-grid").style.display = "none";
    document.querySelector(".top-bar h2").textContent = "İstatistikler";
    document.getElementById("chartSection").style.display = "block";


    fetch("/api/carbon/summary")
        .then(res => res.json())
        .then(summaryData => {
            const labels = Object.keys(summaryData).filter(key => key !== "TOTAL");
            const values = labels.map(label => summaryData[label]);

            const ctx = document.getElementById('carbonChart').getContext('2d');

            // Eğer daha önceden bir grafik varsa sil (üzerine binmesin)
            if (myChart) { myChart.destroy(); }

            myChart = new Chart(ctx, {
                type: 'bar',
                data: {
                    labels: labels,
                    datasets: [{
                        label: 'kg CO2 Salınımı',
                        data: values,
                        backgroundColor: ['#4ec48f','#7b68ee','#4a90e2', '#46c1e1' ],
                        borderRadius: 10
                    }]
                },
                options: {
                    responsive: true,
                    maintainAspectRatio: false,
                    layout: {
                        padding: {
                            left: 20,
                            right: 20,
                            top: 20,
                            bottom: 40
                        }
                    },
                    plugins: {
                        legend: { position: 'top' }
                    },
                    scales: {
                        y: {
                            beginAtZero: true,
                            ticks: {
                                padding: 10
                            }
                        },
                        x: {
                            ticks: {
                                padding: 10
                            }
                        }
                    }
                }
            });
        });
}

function showDashboard() {
    // Menü aktif durumunu güncelle
    document.querySelectorAll('nav ul li').forEach(li => li.classList.remove('active'));
    if (event && event.target) {
        event.target.closest('li').classList.add('active');
    }
    
    // Tüm bölümleri gizle
    document.getElementById("chartSection").style.display = "none";
    document.getElementById("dailySection").style.display = "none";
    
    // Ana sayfa bölümlerini göster
    document.getElementById("infoCards").style.display = "block";
    document.querySelector(".stats-grid").style.display = "grid";
    document.querySelector(".top-bar h2").textContent = "Anasayfa";
}

function loadDailyStats() {
    fetch("/api/carbon/daily")
        .then(res => res.json())
        .then(dailyData => {
            console.log("Günlük Veriler:", dailyData);
            updateDailyCards(dailyData);
        })
        .catch(err => console.error("Günlük Veri Yükleme Hatası:", err));
}

function updateDailyCards(dailyData) {
    console.log("Günlük Veri:", dailyData);
    
    const dailyStatsMap = {
        "Video İzleme": "dailyVideoStat",
        "E-posta": "dailyEmailStat",
        "Online Toplantı": "dailyMeetingStat",
        "Sosyal Medya": "dailySocialStat"
    };
    
    // Tüm günlük kartları göster ve değerleri güncelle
    Object.keys(dailyStatsMap).forEach(activity => {
        const id = dailyStatsMap[activity];
        const value = dailyData[activity] || 0;
        const card = document.getElementById(id);
        
        if (card) {
            // Kartı her zaman göster
            card.style.display = 'block';
            const valueElement = card.querySelector('.value');
            if (valueElement) {
                valueElement.innerHTML = `${value.toFixed(2)} <small>kg</small>`;
            }
        } else {
            console.error("Kart bulunamadı:", id, "aktivite:", activity);
        }
    });
    
    // Toplam kartı
    const total = dailyData["TOTAL"] || 0;
    const totalCard = document.getElementById("dailyTotalStat");
    if (totalCard) {
        totalCard.style.display = 'block';
        const valueElement = totalCard.querySelector('.value');
        if (valueElement) {
            valueElement.innerHTML = `${total.toFixed(2)} <small>kg</small>`;
        }
    } else {
        console.error("Toplam kartı bulunamadı: dailyTotalStat");
    }
}

function calculateDaily() {
    const activity = document.getElementById("activityNameDaily").value;
    const duration = document.getElementById("durationMinutesDaily").value;

    if (!activity || activity === "Seçiniz") {
        alert("Lütfen bir aktivite seçiniz");
        return;
    }

    if (!duration || duration <= 0) {
        alert("Lütfen geçerli bir süre giriniz");
        return;
    }

    fetch("/api/carbon/save", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
            activityName: activity,
            durationMinutes: parseInt(duration)
        })
    })
        .then(res => res.json())
        .then(() => {
            // Günlük verileri yeniden yükle
            loadDailyStats();
            // Ana sayfa istatistiklerini de güncelle
            loadStats();
            // Formu temizle
            document.getElementById("durationMinutesDaily").value = "";
            document.getElementById("activityNameDaily").value = "Seçiniz";
        })
        .catch(err => console.error("Hata:", err));
}

function checkCarbonLimit(total) {
    const alertBox = document.getElementById("alertBox");
    const alertMessage = document.getElementById("alertMessage");
    const limit = 10.0; // Test etmek istersen burayı 0.5 gibi küçük bir sayı yapabilirsin

    if (total > limit) {
        alertBox.style.display = "flex";
        alertMessage.innerHTML = `Bugünkü toplam salınımınız <b>${total.toFixed(2)} kg</b> oldu. 
                                  Biraz fazla karbon ürettik, bugünlük bu kadar teknoloji yeter mi Ayşegül? 🌱`;
    } else {
        alertBox.style.display = "none";
    }
}

function closeAlert() {
    document.getElementById("alertBox").style.display = "none";
}