let myChart = null; // Grafiği her seferinde yeniden yaratmamak için

const statsMap = {
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
            loadStats(); // Kayıttan sonra sayıları tazele
            document.getElementById("durationMinutes").value = "";
        })
        .catch(err => console.error("Hata:", err));
}

function loadStats() {

    fetch("/api/carbon/summary")
        .then(res => res.json())
        .then(summaryData => {
            console.log("Backendden Gelen Özet:", summaryData);

            // Backend'den gelen Map içindeki TOTAL anahtarını alıyoruz
            if (summaryData["TOTAL"] !== undefined) {
                document.getElementById("totalStat").innerHTML =
                    `${summaryData["TOTAL"].toFixed(2)} <small>kg</small>`;
            }

       -
            Object.keys(statsMap).forEach(activity => {
                const id = statsMap[activity];
                const value = summaryData[activity] || 0;
                document.getElementById(id).innerHTML =
                    `${value.toFixed(2)} <small>kg</small>`;
            });
        })
        .catch(err => console.error("Yükleme Hatası:", err));



}
function showStats() {

    document.querySelector(".form-container").style.display = "none";
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
    document.querySelector(".form-container").style.display = "block";
    document.getElementById("chartSection").style.display = "none";
}