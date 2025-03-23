import {useNavigate} from "react-router-dom";
import {
    Avatar,
    Box,
    Button,
    DarkMode,
    Drawer,
    DrawerBody,
    DrawerHeader,
    DrawerOverlay,
    DrawerContent,
    DrawerCloseButton,
    Flex,
    Heading,
    HStack,
    IconButton,
    Menu,
    MenuButton,
    MenuDivider,
    MenuGroup,
    MenuItem,
    MenuList,
    Text,
    useColorMode,
    useDisclosure
} from "@chakra-ui/react";
import {HamburgerIcon} from '@chakra-ui/icons';
import {logout, User} from "../services/userService.js";

export const Navbar = () => {
    const {colorMode, toggleColorMode} = useColorMode();
    const {isOpen, onOpen, onClose} = useDisclosure();
    const navigate = useNavigate();
    const user = User.fromLocalStorage();

    return (
        <Flex
            as="nav"
            align="center"
            justify="space-between"
            padding="1rem"
            bg='#7A3530'
            wrap="wrap"
        >
            {/* Desktop Menu */}
            <Box display={{base: "none", md: "flex"}} flexGrow={1}>
                <Button size='lg' variant="link" color="white" onClick={() => navigate('/dashboard')} margin='10px'>
                    <i className="fa-solid fa-house"/><Text marginX='5px'>Strona główna</Text>
                </Button>

                <Button size='lg' variant="link" color="white" onClick={() => navigate('/tasks')} margin='10px'>
                    <i className="fa-solid fa-fw fa-book-open"/> <Text marginX='5px'>Zbiór zadań</Text>
                </Button>

                <Menu>
                    <MenuButton as={Button} size='lg' variant="link" color='white' marginX='10px'>
                        Moje zadania <i className="fa-solid fa-fw fa-angle-down"/>
                    </MenuButton>
                    <MenuList>
                        {(user.role === "INSTRUCTOR" || user.role === "ADMIN") && (
                            <>
                                <MenuGroup title='Przypisywanie zadań'>
                                    <MenuItem>Zarządzaj</MenuItem>
                                    <MenuItem>Przypisz uczniowi</MenuItem>
                                    <MenuItem>Przypisz grupie</MenuItem>
                                </MenuGroup>
                                <MenuDivider/>
                                <MenuGroup title='Autorskie zadania'>
                                    <MenuItem>Zarządzaj</MenuItem>
                                    <MenuItem>Stwórz</MenuItem>
                                </MenuGroup>
                                <MenuDivider/>

                            </>
                        )}
                        <MenuGroup title='Rozwiązywanie zadań'>
                            <MenuItem onClick={() => navigate('/activeTasks')}>
                                <i className="fa-solid fa-fw fa-clipboard"/><Text marginX='2px'>Aktywne</Text>
                            </MenuItem>

                            <MenuItem onClick={() => navigate('/finishedTasks')}>
                                <i className="fa-solid fa-fw fa-check-double"/><Text marginX='2px'>Rozwiązane</Text>
                            </MenuItem>
                        </MenuGroup>

                    </MenuList>
                </Menu>

                <Button size='lg' variant="link" color="white">
                    <a
                        href="https://docs.google.com/forms/d/e/1FAIpQLScPISDgyrNTrqPQW8ShD_cGJVLRnm_dJu6uyfGnuFkyvbQvSQ/viewform?usp=header"
                        target="_blank"
                        rel="noopener noreferrer"
                        style={{
                            display: 'flex',
                            alignItems: 'center',
                            textDecoration: 'none',
                            color: 'white',
                            margin: '10px',
                            fontSize: '18px'
                        }}
                    >
                        <i className="fa-solid fa-file-pen" style={{marginRight: '5px'}}/>
                        <Text marginX='5px'>Ankieta</Text>
                    </a>
                </Button>

            </Box>

            <Box display={{base: "block", md: "none"}}>
                <IconButton
                    icon={<HamburgerIcon/>}
                    variant="outline"
                    color="white"
                    onClick={onOpen}
                />
            </Box>

            <Drawer isOpen={isOpen} placement="left" onClose={onClose}>
                <DrawerOverlay/>
                <DrawerContent bg="#7A3530" color="white">
                    <DrawerCloseButton/>
                    <DrawerHeader>Menu</DrawerHeader>

                    <DrawerBody>
                        <Button variant="ghost" w="100%" onClick={() => navigate('/dashboard')}>
                            <i className="fa-solid fa-house"/><Text marginLeft='5px'>Strona główna</Text>
                        </Button>

                        <Button variant="ghost" w="100%" onClick={() => navigate('/tasks')}>
                            <i className="fa-solid fa-fw fa-book-open"/><Text marginLeft='5px'>Zbiór zadań</Text>
                        </Button>

                        <Button variant="ghost" w='100%' color="white">
                            <a
                                href="https://docs.google.com/forms/d/e/1FAIpQLScPISDgyrNTrqPQW8ShD_cGJVLRnm_dJu6uyfGnuFkyvbQvSQ/viewform?usp=header"
                                target="_blank"
                                rel="noopener noreferrer"
                                style={{
                                    display: 'flex',
                                    alignItems: 'center',
                                    textDecoration: 'none',
                                    color: 'white',
                                    margin: '10px',
                                    fontSize: '18px'
                                }}
                            >
                                <i className="fa-solid fa-file-pen" style={{marginRight: '5px'}}/>
                                <Text marginX='5px'>Ankieta</Text>
                            </a>
                        </Button>

                        {(user.role === "INSTRUCTOR" || user.role === "ADMIN") && (
                            <>
                                <Heading size='sm' marginTop='15px'>Przypisywanie zadań</Heading>
                                <Button variant="ghost" w="100%">Zarządzaj</Button>
                                <Button variant="ghost" w="100%">Przypisz uczniowi</Button>
                                <Button variant="ghost" w="100%">Przypisz grupie</Button>
                            </>
                        )}


                        <Heading size='sm' marginTop='15px'>Rozwiązywanie zadań</Heading>
                        <Button variant="ghost" w="100%" onClick={() => navigate('/activeTasks')}>
                            Aktywne
                        </Button>

                        <Button variant="ghost" w="100%" onClick={() => navigate('/finishedTasks')}>
                            Rozwiązane
                        </Button>

                        {/* Bottom Section for Profile/Settings/Logout */}
                        <Box mt="5">
                            <HStack>
                                <Avatar size="sm" name={user.username}/>
                                <Text>{user.username}</Text>
                            </HStack>

                            <Button variant="ghost" w="100%" onClick={toggleColorMode}>
                                <i className={'fa-solid fa-fw ' + (colorMode === 'light' ? 'fa-moon' : 'fa-sun')}/>
                                Ustaw {colorMode === 'light' ? 'ciemny' : 'jasny'} motyw
                            </Button>

                            <Button variant="ghost" w="100%" onClick={logout}>
                                <i className="fa-solid fa-right-from-bracket fa-fw"/> Wyloguj się
                            </Button>
                        </Box>
                    </DrawerBody>
                </DrawerContent>
            </Drawer>

            {/* User Profile Menu */}
            <Menu>
                <DarkMode>
                    <MenuButton as={Button}>
                        <HStack>
                            <Heading size='md' color='white'>{user.username}</Heading>
                            <Avatar size='sm' name={user.username}/>
                        </HStack>
                    </MenuButton>
                </DarkMode>

                <MenuList>
                    {/*<MenuItem><i className="fa-solid fa-user fa-fw"/> Profil</MenuItem>*/}

                    <MenuItem onClick={toggleColorMode}>
                        <i className={'fa-solid fa-fw ' + (colorMode === 'light' ? 'fa-moon' : 'fa-sun')}/>
                        Ustaw {colorMode === 'light' ? 'ciemny' : 'jasny'} motyw
                    </MenuItem>

                    {/*<MenuItem><i className='fa-solid fa-gear fa-fw'/> Ustawienia </MenuItem>*/}

                    <MenuItem onClick={logout}><i className="fa-solid fa-right-from-bracket fa-fw"/>
                        Wyloguj się
                    </MenuItem>
                </MenuList>
            </Menu>
        </Flex>
    )
}
