FROM ubuntu:22.04
LABEL maintainer Junling <li@junling.li>

ARG DOCKER_LANG=zh_CN
ARG DOCKER_TIMEZONE=Asia/Shanghai
ARG DOCKER_OTHERPACKAGES="fcitx fcitx-config-gtk fcitx-frontend-all \
        fcitx-frontend-gtk3 fcitx-pinyin fcitx-googlepinyin \
        fcitx-ui-classic im-config fcitx-module-dbus fcitx-module-kimpanel \
        fcitx-module-lua fcitx-module-x11 presage fonts-wqy-microhei \
        language-pack-zh-hans language-pack-gnome-zh-hans"
ARG X11VNC_VERSION=latest

ENV LANG=$DOCKER_LANG.UTF-8 \
    LANGUAGE=$DOCKER_LANG:UTF-8 \
    LC_ALL=$DOCKER_LANG.UTF-8 \
    XMODIFIERS=@im=fcitx

WORKDIR /tmp

ARG DEBIAN_FRONTEND=noninteractive

# Install some required system tools and packages for X Windows and ssh.
# Also remove the message regarding unminimize.
# Note that Ubuntu 22.04 uses snapd for firefox, which does not work properly,
# so we install it from ppa:mozillateam/ppa instead.
RUN apt-get update && \
    DEBIAN_FRONTEND=noninteractive apt-get install -y --no-install-recommends \
        apt-utils \
        apt-file \
        locales \
        language-pack-en && \
    locale-gen $LANG && \
    dpkg-reconfigure -f noninteractive locales && \
    DEBIAN_FRONTEND=noninteractive apt-get install -y --no-install-recommends \
        curl \
        less \
        vim \
        psmisc \
        runit \
        apt-transport-https ca-certificates \
        software-properties-common \
        man \
        sudo \
        rsync \
        libarchive-tools \
        net-tools \
        gpg-agent \
        inetutils-ping \
        csh \
        tcsh \
        zsh zsh-autosuggestions \
        build-essential autoconf automake autotools-dev pkg-config \
        libssl-dev \
        git \
        dos2unix \
        dbus-x11 \
        \
        openssh-server \
        python3 \
        python3-distutils \
        python3-tk \
        python3-dbus \
        \
        xserver-xorg-video-dummy \
        lxde \
        x11-xserver-utils \
        xterm \
        gnome-themes-standard \
        gtk2-engines-pixbuf \
        gtk2-engines-murrine \
        libcanberra-gtk-module libcanberra-gtk3-module \
        fonts-liberation \
        xfonts-base xfonts-100dpi xfonts-75dpi xfonts-scalable xfonts-cyrillic \
        libopengl0 mesa-utils libglu1-mesa libgl1-mesa-dri libjpeg8 libjpeg62 \
        xauth xdg-utils \
        x11vnc \
        \
        $DOCKER_OTHERPACKAGES && \
    chmod 755 /usr/local/share/zsh/site-functions && \
    add-apt-repository -y ppa:mozillateam/ppa && \
    echo 'Package: *' > /etc/apt/preferences.d/mozilla-firefox && \
    echo 'Pin: release o=LP-PPA-mozillateam' >> /etc/apt/preferences.d/mozilla-firefox && \
    echo 'Pin-Priority: 1001' >> /etc/apt/preferences.d/mozilla-firefox && \
    apt install -y firefox firefox-locale-zh-hans && \
    apt-get -y autoremove && \
    ssh-keygen -A && \
    ln -s -f /lib64/ld-linux-x86-64.so.2 /lib64/ld-lsb-x86-64.so && \
    perl -p -i -e 's/#?X11Forwarding\s+\w+/X11Forwarding yes/g; \
        s/#?X11UseLocalhost\s+\w+/X11UseLocalhost no/g; \
        s/#?PasswordAuthentication\s+\w+/PasswordAuthentication no/g; \
        s/#?PermitEmptyPasswords\s+\w+/PermitEmptyPasswords no/g' \
        /etc/ssh/sshd_config && \
    rm -f /etc/update-motd.d/??-unminimize && \
    rm -f /etc/xdg/autostart/lxpolkit.desktop && \
    chmod a-x /usr/bin/lxpolkit && \
    rm -rf /var/lib/apt/lists/* /tmp/* /var/tmp/*

# Install websokify and noVNC
RUN curl -O https://bootstrap.pypa.io/get-pip.py && \
    python3 get-pip.py && \
    pip3 install --no-cache-dir \
        setuptools && \
    pip3 install -U https://github.com/novnc/websockify/archive/refs/tags/v0.10.0.tar.gz && \
    mkdir /usr/local/noVNC && \
    curl -s -L https://github.com/x11vnc/noVNC/archive/refs/heads/x11vnc.zip | \
          bsdtar zxf - -C /usr/local/noVNC --strip-components 1 && \
    (chmod a+x /usr/local/noVNC/utils/launch.sh || \
        (chmod a+x /usr/local/noVNC/utils/novnc_proxy && \
         ln -s -f /usr/local/noVNC/utils/novnc_proxy /usr/local/noVNC/utils/launch.sh)) && \
    rm -rf /tmp/* /var/tmp/*

# Install latest version of x11vnc from source
# Also, fix issue with Shift-Tab not working
# https://askubuntu.com/questions/839842/vnc-pressing-shift-tab-tab-only
RUN apt-get update && \
    apt-get install -y libxtst-dev libssl-dev libvncserver-dev libjpeg-dev && \
    \
    mkdir -p /tmp/x11vnc-${X11VNC_VERSION} && \
    curl -s -L https://github.com/LibVNC/x11vnc/archive/refs/heads/master.zip | \
        bsdtar zxf - -C /tmp/x11vnc-${X11VNC_VERSION} --strip-components 1 && \
    cd /tmp/x11vnc-${X11VNC_VERSION} && \
    bash autogen.sh --prefix=/usr/local CFLAGS='-O2 -fno-common -fno-stack-protector' && \
    make && \
    make install && \
    perl -e 's/,\s*ISO_Left_Tab//g' -p -i /usr/share/X11/xkb/symbols/pc && \
    apt-get -y remove libxtst-dev libssl-dev libvncserver-dev libjpeg-dev && \
    apt-get -y autoremove && \
    ldconfig && \
    rm -rf /var/lib/apt/lists/* /tmp/* /var/tmp/*



RUN apt-get update && \
    apt-get install -y openjdk-17-jdk \
    && rm -rf /var/lib/apt/lists/* /tmp/* /var/tmp/*

########################################################
# Customization for user and location
########################################################
# Set up user so that we do not run as root in DOCKER
ENV DOCKER_USER=ubuntu \
    DOCKER_UID=9999 \
    DOCKER_GID=9999 \
    DOCKER_SHELL=/bin/zsh

ENV DOCKER_GROUP=$DOCKER_USER \
    DOCKER_HOME=/home/$DOCKER_USER \
    SHELL=$DOCKER_SHELL


# Change the default timezone to $DOCKER_TIMEZONE
# Run ldconfig so that /usr/local/lib etc. are in the default
# search path for dynamic linker
RUN groupadd -g $DOCKER_GID $DOCKER_GROUP && \
    useradd -m -u $DOCKER_UID -g $DOCKER_GID -s $DOCKER_SHELL -G sudo $DOCKER_USER && \
    echo "$DOCKER_USER:"`openssl rand -base64 12` | chpasswd && \
    echo "$DOCKER_USER ALL=(ALL) NOPASSWD: ALL" >> /etc/sudoers && \
    echo "$DOCKER_TIMEZONE" > /etc/timezone && \
    ln -s -f /usr/share/zoneinfo/$DOCKER_TIMEZONE /etc/localtime

ADD image/etc /etc
ADD image/usr /usr
ADD image/sbin /sbin
ADD image/home $DOCKER_HOME

# Make home directory readable to work with Singularity
RUN mkdir -p $DOCKER_HOME/.config/mozilla && \
    ln -s -f .config/mozilla $DOCKER_HOME/.mozilla && \
    im-config -n fcitx && \
    echo '@fcitx-autostart' >> $DOCKER_HOME/.config/lxsession/LXDE/autostart && \
    touch $DOCKER_HOME/.sudo_as_admin_successful && \
    mkdir -p $DOCKER_HOME/shared && \
    mkdir -p $DOCKER_HOME/.ssh && \
    mkdir -p $DOCKER_HOME/.log && touch $DOCKER_HOME/.log/vnc.log && \
    chown -R $DOCKER_USER:$DOCKER_GROUP $DOCKER_HOME && \
    chmod -R a+r $DOCKER_HOME && \
    find $DOCKER_HOME -type d -exec chmod a+x {} \;

WORKDIR $DOCKER_HOME

ENV DOCKER_CMD=start_vnc

USER root
ENTRYPOINT ["/sbin/my_init", "--", "/sbin/setuser", "ubuntu"]
CMD ["$DOCKER_CMD"]