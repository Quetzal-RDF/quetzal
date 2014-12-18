VAGRANTFILE_API_VERSION = "2"

Vagrant.configure(VAGRANTFILE_API_VERSION) do |config|
config.vm.provider :virtualbox do |vbox, override|
override.vm.box = "box-cutter/ubuntu1404-docker"
#override.vm.network :private_network, ip: "192.168.50.10"
vbox.customize ["modifyvm", :id, "--memory", 512]
end
end