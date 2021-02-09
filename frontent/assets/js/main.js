let currentView = 'startseite'


$('#nav-button-startseite').click(() => {
    if (currentView !== 'startseite') {
        currentView = 'startseite'
        $('#startseite').show(750)
        $('#mute-channels').hide(750)
    }
})
$('#nav-button-mute-channels').click(() => {
    if (currentView !== 'mute-channels') {
        currentView = 'mute-channels'
        renderChannels()
        $('#startseite').hide(750)
        $('#mute-channels').show(750)
    }
})

async function updateStatus() {
    try {
        await fetch('http://localhost:8021/status')
        $('#status').css('background', 'rgb(0,150,5)')
        $('#status-label').text('Online')
        $('.hidden').removeClass('hidden')
    } catch (e) {
        $('#status').css('background', 'rgb(160,0,0)')
        $('#status-label').text('Offline')
        $('.hidden').addClass('hidden')
    }
}

updateStatus()

function updateRecursive() {
    updateStatus()
    setTimeout(updateRecursive, 30000)
}
updateRecursive()
$('#update-status').click(updateStatus)

function renderChannels() {
    fetch('http://localhost:8021/channels').then(r => r.json()).then((body) => {
        fetch('assets/templates/channels.ejs').then(r => r.text()).then(t => {
            $('#channelContainer').html(ejs.render(t, { 'input': body}));
            $('.channel').children().click((data) => {
                const muted = $(data.currentTarget.parentElement).hasClass('muted')
                const channelId = data.currentTarget.parentElement.firstElementChild.innerText.replace(/\s+/g, '')

                if (muted) {
                    fetch('http://localhost:8021/channels/unmute/' + channelId)
                } else {
                    fetch('http://localhost:8021/channels/mute/' + channelId)
                }
                renderChannels()
            })
        })
    })
}

function renderChannelsRec() {
    if (currentView === 'mute-channels') {
        renderChannels()
    }
    setTimeout(renderChannelsRec, 5000)
}

renderChannelsRec()
